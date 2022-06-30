package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.ExternalAnnotatorBatchInspection;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.php.tools.quality.QualityToolAnnotator;
import com.jetbrains.php.tools.quality.QualityToolAnnotatorInfo;
import com.jetbrains.php.tools.quality.QualityToolValidationGlobalInspection;
import com.jetbrains.php.tools.quality.QualityToolXmlMessageProcessor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.intellij.openapi.util.text.StringUtil.isNotEmpty;
import static com.jetbrains.php.tools.quality.QualityToolAnnotator.updateIfRemoteMappingExists;

public class PhpStanGlobalInspection extends QualityToolValidationGlobalInspection implements ExternalAnnotatorBatchInspection {
  @NonNls public String memoryLimit = "2G";
  public int level = 4;
  public @NlsSafe String config = "";
  public @NlsSafe String autoload = "";
  public boolean transferred = false;

  public static final Key<List<QualityToolXmlMessageProcessor.ProblemDescription>> PHPSTAN_ANNOTATOR_INFO = Key.create("ANNOTATOR_INFO_2");

  @Override
  public void inspectionStarted(@NotNull InspectionManager manager,
                                @NotNull GlobalInspectionContext globalContext,
                                @NotNull ProblemDescriptionsProcessor problemDescriptionsProcessor) {
    super.inspectionStarted(manager, globalContext, problemDescriptionsProcessor);
    final QualityToolAnnotator annotator = getAnnotator();
    final QualityToolAnnotatorInfo info =
      annotator.collectAnnotatorInfo(null, null, globalContext.getProject(), ((InspectionManagerBase)manager).getCurrentProfile(), false);
    if (info != null) {
      manager.getProject().putUserData(ANNOTATOR_INFO, annotator.doAnnotate(info));
    }
  }

  @Override
  public @Nullable LocalInspectionTool getSharedLocalInspectionTool() {
    return new PhpStanValidationInspection();
  }

  @Override
  protected @NotNull QualityToolAnnotator getAnnotator() {
    return PhpStanAnnotatorProxy.INSTANCE;
  }

  @Override
  protected Key<List<QualityToolXmlMessageProcessor.ProblemDescription>> getKey() {
    return PHPSTAN_ANNOTATOR_INFO;
  }

  public List<String> getCommandLineOptions(@NotNull List<String> filePath, @NotNull Project project) {
    @NonNls ArrayList<String> options = new ArrayList<>();
    PhpStanProjectConfiguration configuration = PhpStanProjectConfiguration.getInstance(project);
    options.add("analyze");
    if (isNotEmpty(configuration.getConfig())) {
      options.add("-c");
      options.add(updateIfRemoteMappingExists(configuration.getConfig(), project, PhpStanQualityToolType.INSTANCE));
    }
    else {
      options.add("--level=" + configuration.getLevel());
    }
    if (isNotEmpty(configuration.getAutoload())) {
      options.add("-a");
      options.add(updateIfRemoteMappingExists(configuration.getAutoload(), project, PhpStanQualityToolType.INSTANCE));
    }
    options.add("--memory-limit=" + configuration.getMemoryLimit());
    options.add("--error-format=checkstyle");
    options.add("--no-progress");
    options.add("--no-ansi");
    options.add("--no-interaction");
    List<String> filePaths = ContainerUtil.filter(filePath, Objects::nonNull);
    filePaths = ContainerUtil.map(filePaths, it -> updateIfRemoteMappingExists(it, project, PhpStanQualityToolType.INSTANCE));
    options.addAll(filePaths);
    return options;
  }

  @Override
  public ProblemDescriptor @NotNull [] checkFile(@NotNull PsiFile file,
                                                 @NotNull GlobalInspectionContext context,
                                                 @NotNull InspectionManager manager) {
    ProblemsHolder holder = new ProblemsHolder(manager, file, false);
    super.checkFile(file, manager, holder, context, null);
    return holder.getResultsArray();
  }
}

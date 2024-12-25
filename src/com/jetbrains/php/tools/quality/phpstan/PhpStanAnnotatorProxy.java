package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.codeInspection.InspectionProfile;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.SmartList;
import com.jetbrains.php.tools.quality.QualityToolAnnotator;
import com.jetbrains.php.tools.quality.QualityToolAnnotatorInfo;
import com.jetbrains.php.tools.quality.QualityToolConfiguration;
import com.jetbrains.php.tools.quality.QualityToolMessageProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.intellij.openapi.util.text.StringUtil.isNotEmpty;
import static com.intellij.util.containers.ContainerUtil.*;
import static java.util.Collections.singletonList;

public final class PhpStanAnnotatorProxy extends QualityToolAnnotator<PhpStanValidationInspection> {
  public static final PhpStanAnnotatorProxy INSTANCE = new PhpStanAnnotatorProxy();

  @Override
  protected List<String> getOptions(@Nullable String filePath, @NotNull PhpStanValidationInspection inspection, 
                                    @Nullable InspectionProfile profile, @NotNull Project project) {
    return emptyList();
  }

  @Override
  protected @Nullable List<String> getOptions(@Nullable String filePath,
                                              @NotNull PhpStanValidationInspection inspection,
                                              @Nullable InspectionProfile profile,
                                              @NotNull Project project,
                                              boolean isOnTheFly) {
    final PhpStanGlobalInspection tool = (PhpStanGlobalInspection)getQualityToolType().getGlobalTool(project, profile);
    if (tool == null) {
      return emptyList();
    }

    if (isOnTheFly) {
      return tool.getCommandLineOptions(singletonList(filePath), project);
    }
    PhpStanOptionsConfiguration configuration = PhpStanOptionsConfiguration.getInstance(project);
    return tool.getCommandLineOptions(configuration.isFullProject()
                                      ? new SmartList<>(filePath, project.getBasePath())
                                      : isNotEmpty(configuration.getConfig()) ? emptyList() : concat(map(
                                        ProjectRootManager.getInstance(project).getContentSourceRoots(),
                                        VirtualFile::getPath)), project);
  }

  @Override
  protected QualityToolMessageProcessor createMessageProcessor(@NotNull QualityToolAnnotatorInfo<PhpStanValidationInspection> collectedInfo) {
    return new PhpStanMessageProcessor(collectedInfo);
  }

  @Override
  protected @NotNull QualityToolAnnotatorInfo<PhpStanValidationInspection> createAnnotatorInfo(@Nullable PsiFile file,
                                                                                               PhpStanValidationInspection tool,
                                                                                               InspectionProfile inspectionProfile,
                                                                                               Project project,
                                                                                               QualityToolConfiguration configuration,
                                                                                               boolean isOnTheFly) {
    return new PhpStanQualityToolAnnotatorInfo(file, tool, inspectionProfile, project, configuration, isOnTheFly);
  }

  @Override
  protected @NotNull PhpStanQualityToolType getQualityToolType() {
    return PhpStanQualityToolType.INSTANCE;
  }

  @Override
  public String getPairedBatchInspectionShortName() {
    return getQualityToolType().getInspectionId();
  }

  @Override
  protected boolean showMessage(@NotNull String message) {
    return !message.contains("The Xdebug PHP extension is active, but \"--xdebug\" is not used");
  }
}


package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.codeInspection.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.php.tools.quality.QualityToolAnnotator;
import com.jetbrains.php.tools.quality.QualityToolMessage;
import com.jetbrains.php.tools.quality.QualityToolMessageProcessor;
import com.jetbrains.php.tools.quality.QualityToolValidationGlobalInspection;
import com.jetbrains.php.tools.quality.phpstan.PhpStanMessageProcessor.PhpStanXmlMessageHandler.ProblemDescription;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.intellij.codeInspection.ProblemHighlightType.WEAK_WARNING;
import static com.intellij.openapi.util.text.StringUtil.isNotEmpty;

public class PhpStanGlobalInspection extends QualityToolValidationGlobalInspection {
  public static final Key<List<ProblemDescription>> PHPSTAN_ANNOTATOR_INFO = Key.create("ANNOTATOR_INFO_2");
  public boolean FULL_PROJECT = false;
  @NonNls public String memoryLimit = "2G";
  public int level = 4;
  public String config = "";
  public String autoload = "";

  @Override
  public JComponent createOptionsPanel() {
    final PhpStanOptionsPanel optionsPanel = new PhpStanOptionsPanel(this);
    optionsPanel.init();
    return optionsPanel.getOptionsPanel();
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
  public void checkFile(@NotNull PsiFile file,
                        @NotNull InspectionManager manager,
                        @NotNull ProblemsHolder problemsHolder,
                        @NotNull GlobalInspectionContext globalContext,
                        @NotNull ProblemDescriptionsProcessor problemDescriptionsProcessor) {
    final List<ProblemDescription> data = globalContext.getProject().getUserData(PHPSTAN_ANNOTATOR_INFO);
    if (data == null) {
      return;
    }

    final List<ProblemDescription> fileData = ContainerUtil.filter(data, i -> file.getVirtualFile().getPath().endsWith(i.getFile()));
    for (PhpStanMessageProcessor.PhpStanXmlMessageHandler.ProblemDescription problem : fileData) {
      final Document document = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);
      QualityToolMessage qualityToolMessage;
      if (document != null && problem.getLineNumber() - 1 > 0 && problem.getLineNumber() - 1 < document.getLineCount()) {
        qualityToolMessage = new QualityToolMessage(file, TextRange
          .create(document.getLineStartOffset(problem.getLineNumber() - 1) + problem.getColumn(),
                  document.getLineEndOffset(problem.getLineNumber() - 1)), problem.getSeverity(), problem.getMessage());
      }
      else {
        qualityToolMessage = new QualityToolMessage(file, problem.getLineNumber(), problem.getSeverity(), problem.getMessage());
      }
      final QualityToolMessageProcessor messageProcessor = globalContext.getProject().getUserData(ANNOTATOR_INFO);
      if (messageProcessor != null) {
        problemsHolder.registerProblem(manager.createProblemDescriptor(file, qualityToolMessage.getTextRange(), qualityToolMessage.getMessageText(), WEAK_WARNING, false));
      }
    }
  }

  @Override
  public void inspectionStarted(@NotNull InspectionManager manager,
                                @NotNull GlobalInspectionContext globalContext,
                                @NotNull ProblemDescriptionsProcessor problemDescriptionsProcessor) {
    globalContext.getProject().putUserData(PHPSTAN_ANNOTATOR_INFO, null);
    super.inspectionStarted(manager, globalContext, problemDescriptionsProcessor);
  }

  public List<String> getCommandLineOptions(@NotNull List<String> filePath) {
    @NonNls ArrayList<String> options = new ArrayList<>();
    options.add("analyze");
    options.add("--level=" + level);
    if (isNotEmpty(config)) {
      options.add("-c");
      options.add(config);
    }
    if (isNotEmpty(autoload)) {
      options.add("-a");
      options.add(autoload);
    }
    options.add("--memory-limit=" + memoryLimit);
    options.add("--error-format=checkstyle");
    options.add("--no-progress");
    options.add("--no-ansi");
    options.add("--no-interaction");
    options.addAll(ContainerUtil.filter(filePath, Objects::nonNull));
    return options;
  }
}

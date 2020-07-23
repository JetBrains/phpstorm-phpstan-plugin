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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.intellij.codeInspection.ProblemHighlightType.WEAK_WARNING;

public class PhpStanGlobalInspection extends QualityToolValidationGlobalInspection {
  public static final Key<List<ProblemDescription>> PHPSTAN_ANNOTATOR_INFO = Key.create("ANNOTATOR_INFO_2");

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
}

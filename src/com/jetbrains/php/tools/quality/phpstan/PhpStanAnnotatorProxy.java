package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.execution.ExecutionException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.config.interpreters.PhpSdkFileTransfer;
import com.jetbrains.php.tools.quality.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.jetbrains.php.tools.quality.QualityToolProcessCreator.runToolProcess;

public class PhpStanAnnotatorProxy extends QualityToolAnnotator {
  public final static PhpStanAnnotatorProxy INSTANCE = new PhpStanAnnotatorProxy();

  @Nullable
  @Override
  protected QualityToolConfiguration getConfiguration(@NotNull Project project, @NotNull LocalInspectionTool inspection) {
    try {
      return PhpStanProjectConfiguration.getInstance(project).findSelectedConfiguration(project);
    }
    catch (QualityToolValidationException e) {
      // skip
    }
    return null;
  }

  @NotNull
  @Override
  protected String getInspectionId() {
    return new PhpStanValidationInspection().getID();
  }

  @Override
  protected void runTool(@NotNull QualityToolMessageProcessor messageProcessor,
                         @NotNull final QualityToolAnnotatorInfo collectedInfo,
                         @NotNull PhpSdkFileTransfer transfer) throws ExecutionException {
    PhpStanValidationInspection inspection = (PhpStanValidationInspection)collectedInfo.getInspection();

    final PhpStanBlackList blackList = PhpStanBlackList.getInstance(collectedInfo.getProject());
    runToolProcess(collectedInfo, blackList, messageProcessor, collectedInfo.getProject().getBasePath(), transfer,
                   inspection.getCommandLineOptions(((PhpStanQualityToolAnnotatorInfo)collectedInfo).getDependsOn()));
    if (messageProcessor.getInternalErrorMessage() != null && collectedInfo.isOnTheFly()) {
      if (collectedInfo.isOnTheFly()) {
        final String message = messageProcessor.getInternalErrorMessage().getMessageText();
        showProcessErrorMessage(collectedInfo, blackList, message);
        logWarning(collectedInfo, message, null);
      }
      messageProcessor.setFatalError();
    }
  }

  @Override
  protected QualityToolMessageProcessor createMessageProcessor(@NotNull QualityToolAnnotatorInfo collectedInfo) {
    return new PhpStanMessageProcessor(collectedInfo);
  }

  @NotNull
  @Override
  protected QualityToolAnnotatorInfo createAnnotatorInfo(@NotNull PsiFile file,
                                                         QualityToolValidationInspection tool,
                                                         Project project,
                                                         QualityToolConfiguration configuration,
                                                         boolean isOnTheFly) {
    return new PhpStanQualityToolAnnotatorInfo(file, tool, project, configuration, isOnTheFly);
  }

  @Override
  protected void addAdditionalAnnotatorInfo(QualityToolAnnotatorInfo collectedInfo, QualityToolValidationInspection tool) {
  }
}


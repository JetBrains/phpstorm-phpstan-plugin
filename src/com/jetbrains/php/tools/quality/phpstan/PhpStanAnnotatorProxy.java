package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.execution.ExecutionException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.config.interpreters.PhpSdkFileTransfer;
import com.jetbrains.php.tools.quality.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhpStanAnnotatorProxy extends QualityToolAnnotator {
  public final static PhpStanAnnotatorProxy INSTANCE = new PhpStanAnnotatorProxy();
  private static final Logger LOG = Logger.getInstance(PhpStanAnnotatorProxy.class);
  private static final String TEMP_DIRECTORY = "PhpStan_temp.tmp";

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
  protected String getTemporaryFilesFolder() {
    return TEMP_DIRECTORY;
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
    String workingDirectory = null;
    PhpStanValidationInspection inspection = (PhpStanValidationInspection)collectedInfo.getInspection();
    messageProcessor.addSubstitution(collectedInfo.getFileName(), collectedInfo.getOriginalFileName());

    final PhpStanBlackList blackList = PhpStanBlackList.getInstance(collectedInfo.getProject());
    QualityToolProcessCreator.runToolProcess(collectedInfo, blackList, messageProcessor, workingDirectory, transfer,
                                             inspection.getCommandLineOptions(collectedInfo.getFilePath(), true));
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
    return new PhpStanMessageProcessor(collectedInfo, collectedInfo.getMaxMessagesPerFile());
  }
}


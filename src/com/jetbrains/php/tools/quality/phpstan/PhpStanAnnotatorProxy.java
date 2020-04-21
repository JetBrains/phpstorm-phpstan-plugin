package com.jetbrains.php.tools.quality.phpstan;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.SmartList;
import com.jetbrains.php.config.interpreters.PhpSdkFileTransfer;
import com.jetbrains.php.tools.quality.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.openapi.util.text.StringUtil.equalsIgnoreWhitespaces;
import static com.intellij.util.containers.ContainerUtil.newArrayList;
import static com.jetbrains.php.tools.quality.QualityToolProcessCreator.runToolProcess;

public class PhpStanAnnotatorProxy extends QualityToolAnnotator {
  public final static PhpStanAnnotatorProxy INSTANCE = new PhpStanAnnotatorProxy();
  private static final Logger LOG = Logger.getInstance(PhpStanAnnotatorProxy.class);
  @NonNls private static final String TEMP_DIRECTORY = "PhpStan_temp.tmp";

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


package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.codeInspection.InspectionProfile;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.SmartList;
import com.jetbrains.php.tools.quality.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.intellij.openapi.util.text.StringUtil.isNotEmpty;
import static com.intellij.util.containers.ContainerUtil.*;
import static java.util.Collections.singletonList;

public final class PhpStanAnnotatorProxy extends QualityToolAnnotator<PhpStanValidationInspection> {
  public final static PhpStanAnnotatorProxy INSTANCE = new PhpStanAnnotatorProxy();

  @Override
  protected List<String> getOptions(@Nullable String filePath, @NotNull PhpStanValidationInspection inspection, @NotNull InspectionProfile profile, @NotNull Project project) {
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
    return tool.getCommandLineOptions(tool.FULL_PROJECT
                                      ? new SmartList<>(filePath, project.getBasePath())
                                      : isNotEmpty(tool.config) ? emptyList() : concat(map(
                                        ProjectRootManager.getInstance(project).getContentSourceRoots(),
                                        VirtualFile::getPath)), project);
  }

  @Override
  protected QualityToolMessageProcessor createMessageProcessor(@NotNull QualityToolAnnotatorInfo collectedInfo) {
    return new PhpStanMessageProcessor(collectedInfo);
  }

  @NotNull
  @Override
  protected QualityToolAnnotatorInfo<PhpStanValidationInspection> createAnnotatorInfo(@Nullable PsiFile file,
                                                                                      PhpStanValidationInspection tool,
                                                                                      InspectionProfile inspectionProfile,
                                                                                      Project project,
                                                                                      QualityToolConfiguration configuration,
                                                                                      boolean isOnTheFly) {
    return new PhpStanQualityToolAnnotatorInfo(file, tool, inspectionProfile, project, configuration, isOnTheFly);
  }

  @Override
  protected @NotNull QualityToolType getQualityToolType() {
    return PhpStanQualityToolType.INSTANCE;
  }

  @Override
  public String getPairedBatchInspectionShortName() {
    return getQualityToolType().getInspectionId();
  }
}


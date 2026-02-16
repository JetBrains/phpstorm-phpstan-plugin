package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.codeInspection.InspectionProfile;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Version;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.SmartList;
import com.jetbrains.php.tools.quality.QualityToolRateLimitSettings;
import com.jetbrains.php.tools.quality.RateLimitedQualityToolAnnotator;
import com.jetbrains.php.tools.quality.QualityToolAnnotatorInfo;
import com.jetbrains.php.tools.quality.QualityToolConfiguration;
import com.jetbrains.php.tools.quality.QualityToolMessageProcessor;
import com.jetbrains.php.tools.quality.QualityToolProcessCreator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.openapi.util.text.StringUtil.isNotEmpty;
import static com.intellij.util.containers.ContainerUtil.concat;
import static com.intellij.util.containers.ContainerUtil.emptyList;
import static com.intellij.util.containers.ContainerUtil.map;
import static java.util.Collections.singletonList;

public final class PhpStanAnnotatorProxy extends RateLimitedQualityToolAnnotator<PhpStanValidationInspection> {
  public static final PhpStanAnnotatorProxy INSTANCE = new PhpStanAnnotatorProxy();
  private static final Logger LOG = Logger.getInstance(PhpStanAnnotatorProxy.class);

  private static final Version EDITOR_MODE_VERSION_1X = new Version(1, 12, 27);
  private static final Version EDITOR_MODE_VERSION_2X = new Version(2, 1, 17);
  private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");

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
    return getCommandLineOptionsForMode(filePath, profile, project, isOnTheFly, null);
  }

  @Override
  protected @Nullable List<String> getOptions(@Nullable String filePath,
                                              @NotNull PhpStanValidationInspection inspection,
                                              @Nullable InspectionProfile profile,
                                              @NotNull Project project,
                                              boolean isOnTheFly,
                                              @NotNull QualityToolAnnotatorInfo<PhpStanValidationInspection> annotatorInfo) {
    return getCommandLineOptionsForMode(filePath, profile, project, isOnTheFly, annotatorInfo);
  }

  private @Nullable List<String> getCommandLineOptionsForMode(@Nullable String filePath,
                                                              @Nullable InspectionProfile profile,
                                                              @NotNull Project project,
                                                              boolean isOnTheFly,
                                                              @Nullable QualityToolAnnotatorInfo<PhpStanValidationInspection> annotatorInfo) {
    final PhpStanGlobalInspection tool = (PhpStanGlobalInspection)getQualityToolType().getGlobalTool(project, profile);
    if (tool == null) {
      return emptyList();
    }

    if (isOnTheFly) {
      if (annotatorInfo != null) {
        PhpStanOptionsConfiguration config = PhpStanOptionsConfiguration.getInstance(project);
        VirtualFile originalFile = annotatorInfo.getOriginalFile();
        if (config.isEditorMode() && originalFile != null && supportsEditorMode(getOrDetectVersion(annotatorInfo, project))) {
          return tool.getCommandLineOptions(singletonList(filePath), project, originalFile.getPath());
        }
      }
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
  protected @NotNull QualityToolRateLimitSettings getRateLimitSettings(@NotNull Project project) {
    return PhpStanOptionsConfiguration.getInstance(project).getRateLimitSettings();
  }

  @Override
  public String getPairedBatchInspectionShortName() {
    return getQualityToolType().getInspectionId();
  }

  @Override
  protected boolean showMessage(@NotNull String message) {
    return !message.contains("The Xdebug PHP extension is active, but \"--xdebug\" is not used");
  }

  @VisibleForTesting
  public static boolean supportsEditorMode(@Nullable Version version) {
    if (version == null) return false;
    if (version.major >= 2) return version.compareTo(EDITOR_MODE_VERSION_2X) >= 0;
    return version.compareTo(EDITOR_MODE_VERSION_1X) >= 0;
  }

  private @Nullable Version getOrDetectVersion(@NotNull QualityToolAnnotatorInfo<PhpStanValidationInspection> annotatorInfo,
                                               @NotNull Project project) {
    QualityToolConfiguration configuration = getConfiguration(project, annotatorInfo.getInspection());
    if (!(configuration instanceof PhpStanConfiguration phpStanConfig)) return null;

    Version cached = phpStanConfig.getVersion();
    if (cached != null) return cached;

    try {
      ProcessOutput output = QualityToolProcessCreator.getToolOutput(project, annotatorInfo.getInterpreterId(), annotatorInfo.getToolPath(),
        5000, PhpStanConfigurationBaseManager.PHP_STAN, null, "--version", "--no-ansi"
      );
      String stdout = output.getStdout().trim();
      Version detected = parseVersionFromOutput(stdout);
      if (detected != null) {
        phpStanConfig.setVersion(detected);
      }
      return detected;
    }
    catch (ExecutionException e) {
      LOG.info("Failed to detect PHPStan version", e);
      return null;
    }
  }

  @VisibleForTesting
  public static @Nullable Version parseVersionFromOutput(@NotNull String output) {
    Matcher matcher = VERSION_PATTERN.matcher(output);

    if (matcher.find()) {
      try {
        int major = !matcher.group(1).isEmpty() ? Integer.parseInt(matcher.group(1)) : 0;
        int minor = !matcher.group(2).isEmpty() ? Integer.parseInt(matcher.group(2)) : 0;
        int patch = !matcher.group(3).isEmpty() ? Integer.parseInt(matcher.group(3)) : 0;
        return new Version(major, minor, patch);
      }
      catch (NumberFormatException e) {
        return null;
      }
    }

    return null;
  }
}

package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.codeInspection.InspectionProfile;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.SmartList;
import com.jetbrains.php.tools.quality.QualityToolRateLimitSettings;
import com.jetbrains.php.tools.quality.RateLimitedQualityToolAnnotator;
import com.jetbrains.php.tools.quality.QualityToolAnnotatorInfo;
import com.jetbrains.php.tools.quality.QualityToolConfiguration;
import com.jetbrains.php.tools.quality.QualityToolMessageProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.intellij.openapi.util.text.StringUtil.isNotEmpty;
import static com.intellij.util.containers.ContainerUtil.concat;
import static com.intellij.util.containers.ContainerUtil.emptyList;
import static com.intellij.util.containers.ContainerUtil.map;
import static java.util.Collections.singletonList;

public final class PhpStanAnnotatorProxy extends RateLimitedQualityToolAnnotator<PhpStanValidationInspection> {
  public static final PhpStanAnnotatorProxy INSTANCE = new PhpStanAnnotatorProxy();
  
  // Map to store original file paths for PHPStan editor mode support
  // Key: temp file path, Value: original file path
  // See: https://phpstan.org/user-guide/editor-mode
  private static final Map<String, String> ORIGINAL_FILE_PATHS = new ConcurrentHashMap<>();

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
      // Use PHPStan editor mode with --tmp-file and --instead-of for proper ignore handling
      // Extract relative path from temp file path to look up original
      String originalFilePath = findOriginalFilePath(filePath);
      return tool.getCommandLineOptions(filePath, originalFilePath, project);
    }
    PhpStanOptionsConfiguration configuration = PhpStanOptionsConfiguration.getInstance(project);
    return tool.getCommandLineOptions(configuration.isFullProject()
                                      ? new SmartList<>(filePath, project.getBasePath())
                                      : isNotEmpty(configuration.getConfig()) ? emptyList() : concat(map(
                                        ProjectRootManager.getInstance(project).getContentSourceRoots(),
                                        VirtualFile::getPath)), project);
  }
  
  /**
   * Finds the original file path from a temp file path.
   * Temp files preserve the relative path structure after the temp folder.
   */
  private @Nullable String findOriginalFilePath(@Nullable String tempFilePath) {
    if (tempFilePath == null) return null;
    
    // Look for the original path in our map
    // The key is the relative path which should be present in both paths
    for (Map.Entry<String, String> entry : ORIGINAL_FILE_PATHS.entrySet()) {
      String relativePath = entry.getKey();
      if (tempFilePath.endsWith(relativePath) || tempFilePath.contains("/" + relativePath) || tempFilePath.contains("\\" + relativePath)) {
        return entry.getValue();
      }
    }
    return null;
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
    // Store original file path for PHPStan editor mode (--tmp-file / --instead-of)
    if (file != null && file.getVirtualFile() != null) {
      String originalPath = file.getVirtualFile().getPath();
      String basePath = project.getBasePath();
      
      // Calculate relative path from project base
      String relativePath = originalPath;
      if (basePath != null && originalPath.startsWith(basePath)) {
        relativePath = originalPath.substring(basePath.length());
        if (relativePath.startsWith("/") || relativePath.startsWith("\\")) {
          relativePath = relativePath.substring(1);
        }
      } else {
        // If not under project, just use the filename as a fallback key
        relativePath = file.getName();
      }
      
      // Store mapping: relative path -> original absolute path
      ORIGINAL_FILE_PATHS.put(relativePath, originalPath);
      
      // Also store by filename as a fallback
      ORIGINAL_FILE_PATHS.put(file.getName(), originalPath);
    }
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
}

package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.codeInspection.GlobalInspectionContext;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.InspectionManagerBase;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptionsProcessor;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.ExternalAnnotatorBatchInspection;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
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
  private static final Logger LOG = Logger.getInstance(PhpStanGlobalInspection.class);
  
  // Set to true to show notifications with the PHPStan command for debugging
  private static final boolean DEBUG_SHOW_COMMAND_NOTIFICATION = false;
  
  public boolean FULL_PROJECT = false;
  public @NonNls String memoryLimit = "2G";
  public int level = 4;
  public @NlsSafe String config = "";
  public @NlsSafe String autoload = "";
  public static final Key<List<QualityToolXmlMessageProcessor.ProblemDescription>> PHPSTAN_ANNOTATOR_INFO = Key.create("ANNOTATOR_INFO_2");

  @Override
  public void inspectionStarted(@NotNull InspectionManager manager,
                                @NotNull GlobalInspectionContext globalContext,
                                @NotNull ProblemDescriptionsProcessor problemDescriptionsProcessor) {
    super.inspectionStarted(manager, globalContext, problemDescriptionsProcessor);
    final PhpStanAnnotatorProxy annotator = getAnnotator();
    final QualityToolAnnotatorInfo<PhpStanValidationInspection> info =
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
  protected @NotNull PhpStanAnnotatorProxy getAnnotator() {
    return PhpStanAnnotatorProxy.INSTANCE;
  }

  @Override
  protected Key<List<QualityToolXmlMessageProcessor.ProblemDescription>> getKey() {
    return PHPSTAN_ANNOTATOR_INFO;
  }

  /**
   * Get command line options for PHPStan on-the-fly analysis.
   * Uses PHPStan's editor mode with --tmp-file and --instead-of for proper ignore handling
   * when the PHPStan version supports it (1.12.27+, 2.1.17+, or 3.x+).
   * See: https://phpstan.org/user-guide/editor-mode
   *
   * @param tmpFilePath      The temporary file path containing the current editor content
   * @param originalFilePath The original file path in the project
   * @param project          The current project
   * @return Command line options for PHPStan
   */
  public List<String> getCommandLineOptions(@Nullable String tmpFilePath, @Nullable String originalFilePath, @NotNull Project project) {
    @NonNls ArrayList<String> options = new ArrayList<>();
    PhpStanOptionsConfiguration configuration = PhpStanOptionsConfiguration.getInstance(project);
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
    
    // Check if the PHPStan version supports editor mode
    PhpStanConfiguration toolConfiguration = PhpStanConfigurationManager.getInstance(project).getLocalSettings();
    String version = toolConfiguration.getVersion();
    
    // Auto-detect version if not already stored (e.g., after plugin upgrade)
    if (version == null && isNotEmpty(toolConfiguration.getToolPath())) {
      version = PhpStanVersionSupport.detectVersion(toolConfiguration.getToolPath());
      if (version != null) {
        toolConfiguration.setVersion(version);
        LOG.info("Auto-detected and stored PHPStan version: " + version);
      }
    }
    
    boolean supportsEditorMode = PhpStanVersionSupport.supportsEditorMode(version);
    
    // PHPStan Editor Mode: use --tmp-file and --instead-of when both paths are available
    // and the PHPStan version supports it (1.12.27+, 2.1.17+, or 3.x+)
    // This ensures ignoreErrors entries work correctly based on the original file path
    if (supportsEditorMode && isNotEmpty(tmpFilePath) && isNotEmpty(originalFilePath)) {
      options.add("--tmp-file");
      options.add(updateIfRemoteMappingExists(tmpFilePath, project, PhpStanQualityToolType.INSTANCE));
      options.add("--instead-of");
      options.add(updateIfRemoteMappingExists(originalFilePath, project, PhpStanQualityToolType.INSTANCE));
      // In editor mode, analyze the original file path (PHPStan uses tmp-file contents instead)
      options.add(updateIfRemoteMappingExists(originalFilePath, project, PhpStanQualityToolType.INSTANCE));
    } else if (isNotEmpty(tmpFilePath)) {
      // Fallback to old behavior if editor mode not supported or original path not available
      options.add(updateIfRemoteMappingExists(tmpFilePath, project, PhpStanQualityToolType.INSTANCE));
    }
    
    // Log the command for debugging
    String toolPath = toolConfiguration.getToolPath();
    String commandLine = toolPath + " " + String.join(" ", options);
    LOG.info("PHPStan command: " + commandLine);
    
    if (DEBUG_SHOW_COMMAND_NOTIFICATION) {
      Notifications.Bus.notify(new Notification(
        "PHPStan",
        "PHPStan Command",
        "<html><body style='word-wrap: break-word;'><code>" + commandLine + "</code></body></html>",
        NotificationType.INFORMATION
      ), project);
    }
    
    return options;
  }

  public List<String> getCommandLineOptions(@NotNull List<String> filePath, @NotNull Project project) {
    @NonNls ArrayList<String> options = new ArrayList<>();
    PhpStanOptionsConfiguration configuration = PhpStanOptionsConfiguration.getInstance(project);
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

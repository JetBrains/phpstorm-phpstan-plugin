package com.jetbrains.php.tools.quality.phpstan;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.profile.codeInspection.InspectionProfileManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.util.Consumer;
import com.jetbrains.php.composer.ComposerDataService;
import com.jetbrains.php.composer.actions.log.ComposerLogMessageBuilder;
import com.jetbrains.php.tools.quality.QualityToolConfigurationManager;
import com.jetbrains.php.tools.quality.QualityToolsComposerConfig;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

import static com.intellij.openapi.util.text.StringUtil.*;
import static com.jetbrains.php.composer.ComposerConfigUtils.parseJson;
import static com.jetbrains.php.tools.quality.phpstan.PhpStanOpenSettingsProvider.PHP_STAN_OPEN_SETTINGS_PROVIDER;

public class PhpStanComposerConfig extends QualityToolsComposerConfig<PhpStanConfiguration, PhpStanValidationInspection> {
  private static final @NonNls String PACKAGE = "phpstan/phpstan";
  private static final @NonNls String RELATIVE_PATH = "bin/phpstan" + (SystemInfo.isWindows ? ".bat" : "");
  private static final @NonNls String PHPSTAN_NEON = "phpstan.neon";

  public PhpStanComposerConfig() {
    super(PACKAGE, RELATIVE_PATH);
  }

  @Override
  public String getQualityInspectionShortName() {
    return PhpStanQualityToolType.INSTANCE.getInspectionId();
  }
  
  @Override
  protected boolean applyRulesetFromComposer(@NotNull Project project, PhpStanConfiguration configuration) {
    final String configPath = ComposerDataService.getInstance(project).getConfigPath();
    PhpStanOptionsConfiguration projectConfiguration = PhpStanOptionsConfiguration.getInstance(project);

    final VirtualFile config = LocalFileSystem.getInstance().refreshAndFindFileByPath(configPath);
    if (config == null) return false;

    final String ruleset = getRuleset(config);
    if (ruleset == null) return false;
    final VirtualFile customRulesetFile = detectCustomRulesetFile(config.getParent(), ruleset);
    final boolean customRulesetChanged = customRulesetFile != null &&
                                         modifyRulesetPhpStanInspectionSetting(project, tool -> applyRuleset(projectConfiguration,
                                                                                                             customRulesetFile.getPath()));

    final String memoryLimit = getMemoryLimit(config);
    final boolean memoryLimitChanged =
      memoryLimit != null && modifyRulesetPhpStanInspectionSetting(project, tool -> applyMemoryLimit(projectConfiguration, memoryLimit));
    
    return customRulesetChanged || memoryLimitChanged;
  }

  @Override
  protected void applyInspectionSettingsFromComposer(Project project, PhpStanConfiguration configuration) {
    final String configPath = ComposerDataService.getInstance(project).getConfigPath();
    final VirtualFile config = LocalFileSystem.getInstance().refreshAndFindFileByPath(configPath);
    if (config == null) return;

    final String memoryLimit = getMemoryLimit(config);
    if (memoryLimit != null) {
      modifyRulesetPhpStanInspectionSetting(project,
                                            tool -> applyMemoryLimit(PhpStanOptionsConfiguration.getInstance(project), memoryLimit));
    }
  }

  private @Nullable String getMemoryLimit(VirtualFile config) {
    JsonElement element;
    try {
      element = parseJson(config);
    }
    catch (IOException | JsonParseException e) {
      return null;
    }

    if (element instanceof JsonObject) {
      final JsonElement scriptElement = ((JsonObject)element).get("scripts");
      if (scriptElement != null) {
        final Ref<String> result = new Ref<>();
        parse(scriptElement, result, (el, res) -> parseLimit(el, result));
        return result.isNull() ? null : result.get();
      }
    }
    return null;
  }

  private static void parseLimit(JsonElement el, Ref<String> result) {
    final String string = el.getAsString();
    if (string != null && string.contains("phpstan")) {
      final List<String> split = split(string, " ");
      for (String arg: split) {
        final String prefix = "--memory-limit=";
        if (startsWith(arg, prefix)) {
          result.set(trimStart(arg, prefix));
          return;
        }
        final int index = split.indexOf(arg);
        if (StringUtil.equals(arg, "--memory-limit") && index < split.size() - 1) {
          result.set(split.get(index + 1));
          return;
        }
      }
    }
  }

  @Override
  protected boolean applyRulesetFromRoot(@NotNull Project project) {
    VirtualFile customRulesetFile = detectCustomRulesetFile(project.getBaseDir(), PHPSTAN_NEON);
    if(customRulesetFile == null){
      customRulesetFile = detectCustomRulesetFile(project.getBaseDir(), PHPSTAN_NEON + ".dist");
    }

    if (customRulesetFile != null) {
      final String path = customRulesetFile.getPath();
      return modifyRulesetPhpStanInspectionSetting(project, tool -> applyRuleset(PhpStanOptionsConfiguration.getInstance(project), path));
    }
    return false;
  }

  @Override
  protected void checkComposerScriptsLeaves(JsonElement element, Ref<String> result) {
    final String string = element.getAsString();
    if (string != null && string.contains("phpstan")) {
      final List<String> split = split(string, " ");
      for (String arg: split) {
        final String prefix = "--configuration=";
        if (startsWith(arg, prefix)) {
          result.set(trimStart(arg, prefix));
          return;
        }
        final int index = split.indexOf(arg);
        if (StringUtil.equals(arg, "-c") && index < split.size() - 1) {
          result.set(split.get(index + 1));
          return;
        }
      }
    }
  }

  @Override
  public @Nullable ComposerLogMessageBuilder.Settings getSettings() {
    return PHP_STAN_OPEN_SETTINGS_PROVIDER;
  }

  private static void applyRuleset(PhpStanOptionsConfiguration configuration, @NlsSafe String customRuleset) {
    configuration.setConfig(customRuleset);
  }

  private static void applyMemoryLimit(PhpStanOptionsConfiguration configuration, @NlsSafe String memoryLimit) {
    configuration.setMemoryLimit(memoryLimit);
  }

  protected boolean modifyRulesetPhpStanInspectionSetting(@NotNull Project project, @NotNull Consumer<PhpStanGlobalInspection> consumer) {
    VirtualFile projectDir = project.getBaseDir();
    if (projectDir == null) return false;

    PsiDirectory file = ReadAction.compute(() -> PsiManager.getInstance(project).findDirectory(projectDir));
    if (file != null) {
      Key<PhpStanGlobalInspection> key = Key.create(PhpStanQualityToolType.INSTANCE.getInspectionId());
      InspectionProfileManager.getInstance(project).getCurrentProfile().modifyToolSettings(key, file, consumer);
      return true;
    }
    return false;
  }

  @Override
  public @NotNull QualityToolConfigurationManager<PhpStanConfiguration> getConfigurationManager(@NotNull Project project) {
    return PhpStanConfigurationManager.getInstance(project);
  }
}
package com.jetbrains.php.tools.quality.phpstan;

import com.google.gson.JsonElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.php.composer.actions.log.ComposerLogMessageBuilder;
import com.jetbrains.php.tools.quality.QualityToolConfigurationManager;
import com.jetbrains.php.tools.quality.QualityToolsComposerConfig;
import com.jetbrains.php.util.PhpOsUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.jetbrains.php.tools.quality.phpstan.PhpStanOpenSettingsProvider.PHP_STAN_OPEN_SETTINGS_PROVIDER;

public class PhpStanComposerConfig extends QualityToolsComposerConfig<PhpStanConfiguration, PhpStanValidationInspection> {
  private static final @NonNls String PACKAGE = "phpstan/phpstan";
  private static final @NonNls String PHPSTAN_NEON = "phpstan.neon";

  public PhpStanComposerConfig() {
    super(PACKAGE);
  }

  @Override
  protected @NotNull String getRelativePath(@Nullable Project project) {
    return "bin/phpstan" + (PhpOsUtil.isWindows(project) ? ".bat" : "");
  }

  @Override
  public String getQualityInspectionShortName() {
    return PhpStanQualityToolType.INSTANCE.getInspectionId();
  }
  
  @Override
  protected boolean applyRulesetFromComposer(@NotNull Project project, PhpStanConfiguration configuration) {
    final VirtualFile config = findComposerConfig(project);
    if (config == null) return false;

    final String ruleset = getRuleset(config);
    if (ruleset == null) return false;

    final VirtualFile customRulesetFile = detectCustomRulesetFile(config.getParent(), ruleset);
    final boolean customRulesetChanged = customRulesetFile != null && applyRuleset(project, customRulesetFile.getPath());
    return applyMemoryLimit(project, config) || customRulesetChanged;
  }

  @Override
  protected void applyInspectionSettingsFromComposer(Project project, PhpStanConfiguration configuration) {
    final VirtualFile config = findComposerConfig(project);
    if (config != null) {
      applyMemoryLimit(project, config);
    }
  }

  @Override
  protected boolean applyRulesetFromRoot(@NotNull Project project) {
    final VirtualFile customRulesetFile = findRootRulesetFile(project, PHPSTAN_NEON);
    return customRulesetFile != null && applyRuleset(project, customRulesetFile.getPath());
  }

  @Override
  protected void checkComposerScriptsLeaves(JsonElement element, Ref<String> result) {
    final String value = findScriptOptionValue(element.getAsString(), "phpstan", "--configuration=", "-c");
    if (value != null) {
      result.set(value);
    }
  }

  @Override
  public @Nullable ComposerLogMessageBuilder.Settings getSettings() {
    return PHP_STAN_OPEN_SETTINGS_PROVIDER;
  }

  private boolean applyRuleset(@NotNull Project project, @NotNull @NlsSafe String customRuleset) {
    return modifyRulesetInspectionSetting(project, () -> PhpStanOptionsConfiguration.getInstance(project).setConfig(customRuleset));
  }

  private boolean applyMemoryLimit(@NotNull Project project, @NotNull VirtualFile config) {
    final String memoryLimit = findInComposerScripts(
      config, (element, result) -> {
        final String value = findScriptOptionValue(element.getAsString(), "phpstan", "--memory-limit=", "--memory-limit");
        if (value != null) {
          result.set(value);
        }
      });
    return memoryLimit != null &&
           modifyRulesetInspectionSetting(project, () -> PhpStanOptionsConfiguration.getInstance(project).setMemoryLimit(memoryLimit));
  }

  @Override
  public @NotNull QualityToolConfigurationManager<PhpStanConfiguration> getConfigurationManager(@NotNull Project project) {
    return PhpStanConfigurationManager.getInstance(project);
  }
}
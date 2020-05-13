package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.php.composer.actions.log.ComposerLogMessageBuilder;
import com.jetbrains.php.tools.quality.QualityToolConfigurationManager;
import com.jetbrains.php.tools.quality.QualityToolsComposerConfig;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.jetbrains.php.composer.actions.log.ComposerLogMessageBuilder.Settings.PHPSTAN;

public class PhpStanComposerConfig extends QualityToolsComposerConfig<PhpStanConfiguration, PhpStanValidationInspection> {
  @NonNls private static final String PACKAGE = "phpstan/phpstan";
  @NonNls private static final String RELATIVE_PATH = "bin/phpstan";
  @NonNls private static final String PHPSTAN_NEON = "phpstan.neon";
  private static final PhpStanValidationInspection PHP_STAN_VALIDATION_INSPECTION = new PhpStanValidationInspection();


  public PhpStanComposerConfig() {
    super(PACKAGE, RELATIVE_PATH);
  }

  @Override
  protected ComposerLogMessageBuilder.Settings getQualityToolsInspectionSettings() {
    return null;
  }

  @Override
  public PhpStanValidationInspection getQualityInspection() {
    return PHP_STAN_VALIDATION_INSPECTION;
  }

  @Override
  public ComposerLogMessageBuilder.Settings getSettings() {
    return PHPSTAN;
  }

  @Override
  protected boolean applyRulesetFromComposer(@NotNull Project project, PhpStanConfiguration configuration) {
    return false;
  }

  @Override
  protected boolean applyRulesetFromRoot(@NotNull Project project) {
    VirtualFile customRulesetFile = detectCustomRulesetFile(project.getBaseDir(), PHPSTAN_NEON);
    if(customRulesetFile == null){
      customRulesetFile = detectCustomRulesetFile(project.getBaseDir(), PHPSTAN_NEON + ".dist");
    }

    if (customRulesetFile != null) {
      final String path = customRulesetFile.getPath();
      return modifyRulesetInspectionSetting(project, tool -> applyRuleset(tool, path));
    }
    return false;
  }


  private static void applyRuleset(PhpStanValidationInspection tool, String customRuleset) {
    tool.config = customRuleset;
  }

  @NotNull
  @Override
  protected QualityToolConfigurationManager<PhpStanConfiguration> getConfigurationManager(@NotNull Project project) {
    return PhpStanConfigurationManager.getInstance(project);
  }
}
package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.composer.actions.log.ComposerLogMessageBuilder;
import com.jetbrains.php.tools.quality.QualityToolConfigurationManager;
import com.jetbrains.php.tools.quality.QualityToolsComposerConfig;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.jetbrains.php.composer.actions.log.ComposerLogMessageBuilder.Settings.PHPSTAN;

public class PhpStanComposerConfig extends QualityToolsComposerConfig<PhpStanConfiguration, PhpStanValidationInspection> {
  @NonNls private static final String PACKAGE = "phpstan/phpstan";
  @NonNls private static final String RELATIVE_PATH = "phpstan/phpstan/bin/phpstan";
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
    return true;
  }

  @NotNull
  @Override
  protected QualityToolConfigurationManager<PhpStanConfiguration> getConfigurationManager(@NotNull Project project) {
    return PhpStanConfigurationManager.getInstance(project);
  }
}
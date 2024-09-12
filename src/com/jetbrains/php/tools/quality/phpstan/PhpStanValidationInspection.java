package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.util.NlsSafe;
import com.jetbrains.php.tools.quality.QualityToolValidationInspection;
import org.jetbrains.annotations.NotNull;

import static com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurationBaseManager.PHP_STAN;

@SuppressWarnings("InspectionDescriptionNotFoundInspection")
public class PhpStanValidationInspection extends QualityToolValidationInspection<PhpStanValidationInspection> {

  @NotNull
  @Override
  protected PhpStanAnnotatorProxy getAnnotator() {
    return PhpStanAnnotatorProxy.INSTANCE;
  }

  @Override
  public @NlsSafe String getToolName() {
    return PHP_STAN;
  }
}

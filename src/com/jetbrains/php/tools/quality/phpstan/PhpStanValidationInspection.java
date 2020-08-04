package com.jetbrains.php.tools.quality.phpstan;

import com.jetbrains.php.tools.quality.QualityToolAnnotator;
import com.jetbrains.php.tools.quality.QualityToolValidationInspection;
import org.jetbrains.annotations.NotNull;

import static com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurationBaseManager.PHP_STAN;

@SuppressWarnings("InspectionDescriptionNotFoundInspection")
public class PhpStanValidationInspection extends QualityToolValidationInspection {

  @NotNull
  @Override
  protected QualityToolAnnotator getAnnotator() {
    return PhpStanAnnotatorProxy.INSTANCE;
  }

  @Override
  public String getToolName() {
    return PHP_STAN;
  }
}

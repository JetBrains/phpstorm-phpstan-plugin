package com.jetbrains.php.tools.quality.phpstan;

import com.jetbrains.php.tools.quality.QualityToolValidationInspection;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("InspectionDescriptionNotFoundInspection")
public class PhpStanValidationInspection extends QualityToolValidationInspection<PhpStanValidationInspection> {

  @Override
  protected @NotNull PhpStanAnnotatorProxy getAnnotator() {
    return PhpStanAnnotatorProxy.INSTANCE;
  }
}

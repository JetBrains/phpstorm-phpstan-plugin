package com.jetbrains.php.tools.quality.phpstan;

import com.jetbrains.php.tools.quality.QualityToolType;
import org.jetbrains.annotations.NotNull;

import static com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurationBaseManager.PHP_STAN;

public class PhpStanQualityToolType extends QualityToolType {
  @NotNull
  @Override
  public String getDisplayName() {
    return PHP_STAN;
  }
}

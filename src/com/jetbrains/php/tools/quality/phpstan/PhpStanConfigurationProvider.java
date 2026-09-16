package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.jetbrains.php.tools.quality.QualityToolConfigurationProvider;
import org.jetbrains.annotations.Nullable;

import static com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurationBaseManager.PHP_STAN;

public abstract class PhpStanConfigurationProvider extends QualityToolConfigurationProvider<PhpStanConfiguration> {
  private static final ExtensionPointName<PhpStanConfigurationProvider> EP_NAME =
    ExtensionPointName.create("com.jetbrains.php.tools.quality.PhpStan.PhpStanConfigurationProvider");

  public static @Nullable PhpStanConfigurationProvider getInstances() {
    return getSingleInstance(EP_NAME, PHP_STAN);
  }
}

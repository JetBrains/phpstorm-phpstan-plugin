package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.jetbrains.php.tools.quality.QualityToolConfigurationProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class PhpStanConfigurationProvider extends QualityToolConfigurationProvider<PhpStanConfiguration> {
  private static final Logger LOG = Logger.getInstance(PhpStanConfigurationProvider.class);
  private static final ExtensionPointName<PhpStanConfigurationProvider> EP_NAME =
    ExtensionPointName.create("com.jetbrains.php.tools.quality.PhpStan.PhpStanConfigurationProvider");

  @Nullable
  public static PhpStanConfigurationProvider getInstances() {
    List<PhpStanConfigurationProvider> extensions = EP_NAME.getExtensionList();
    if (extensions.size() > 1) {
      LOG.error("Several providers for remote PHPStan configuration was found");
    }
    return extensions.isEmpty() ? null : extensions.get(0);
  }
}

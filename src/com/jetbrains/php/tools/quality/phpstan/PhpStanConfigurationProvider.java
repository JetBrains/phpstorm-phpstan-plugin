package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.util.NullableFunction;
import com.jetbrains.php.tools.quality.QualityToolConfigurationProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PhpStanConfigurationProvider extends QualityToolConfigurationProvider<PhpStanConfiguration> {
  private static final Logger LOG = Logger.getInstance(PhpStanConfigurationProvider.class);
  private static final ExtensionPointName<PhpStanConfigurationProvider> EP_NAME =
    ExtensionPointName.create("com.jetbrains.php.tools.quality.PhpStan.PhpStanConfigurationProvider");

  protected void fillSettingsByDefaultValue(@NotNull PhpStanConfiguration settings,
                                            @NotNull NullableFunction<String, String> preparePath,
                                            @NotNull Project project) {
    final PhpStanConfiguration localConfiguration = PhpStanConfigurationManager.getInstance(project).getLocalSettings();
    super.fillSettingsByDefaultValue(settings, localConfiguration, preparePath);
  }

  @Nullable
  public static PhpStanConfigurationProvider getInstances() {
    final PhpStanConfigurationProvider[] extensions = EP_NAME.getExtensions();
    if (extensions.length > 1) {
      LOG.error("Several providers for remote PHPStan configuration was found");
    }
    return extensions.length == 0 ? null : extensions[0];
  }
}

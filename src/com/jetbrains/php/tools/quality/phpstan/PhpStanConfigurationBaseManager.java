package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.util.xmlb.XmlSerializer;
import com.jetbrains.php.tools.quality.QualityToolConfigurationBaseManager;
import com.jetbrains.php.tools.quality.QualityToolConfigurationProvider;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhpStanConfigurationBaseManager extends QualityToolConfigurationBaseManager<PhpStanConfiguration> {
  @NonNls private static final String PHP_STAN_PATH = "PhpStanPath";
  @NonNls private static final String PHP_STAN = "PHPStan";
  @NonNls private static final String ROOT_NAME = "PhpStan_settings";

  @Override
  @NotNull
  protected PhpStanConfiguration createLocalSettings() {
    return new PhpStanConfiguration();
  }

  @Override
  @NotNull
  protected String getQualityToolName() {
    return PHP_STAN;
  }

  @NotNull
  @Override
  protected String getOldStyleToolPathName() {
    return PHP_STAN_PATH;
  }

  @NotNull
  @Override
  protected String getConfigurationRootName() {
    return ROOT_NAME;
  }

  @Nullable
  @Override
  protected QualityToolConfigurationProvider<PhpStanConfiguration> getConfigurationProvider() {
    return PhpStanConfigurationProvider.getInstances();
  }

  @Override
  @Nullable
  protected PhpStanConfiguration loadLocal(Element element) {
    return XmlSerializer.deserialize(element, PhpStanConfiguration.class);
  }

  public static PhpStanConfigurationBaseManager getInstance() {
    return ServiceManager.getService(PhpStanConfigurationBaseManager.class);
  }
}

package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.util.xmlb.XmlSerializer;
import com.jetbrains.php.tools.quality.QualityToolConfigurationBaseManager;
import com.jetbrains.php.tools.quality.QualityToolType;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhpStanConfigurationBaseManager extends QualityToolConfigurationBaseManager<PhpStanConfiguration> {
  private static final @NlsSafe  String PHP_STAN_PATH = "PhpStanPath";
  public static final @NlsSafe String PHP_STAN = "PHPStan";
  private static final @NlsSafe String ROOT_NAME = "PhpStan_settings";

  @Override
  protected @NotNull QualityToolType<PhpStanConfiguration> getQualityToolType() {
    return PhpStanQualityToolType.INSTANCE;
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

  @Override
  @Nullable
  protected PhpStanConfiguration loadLocal(Element element) {
    return XmlSerializer.deserialize(element, PhpStanConfiguration.class);
  }

  public static PhpStanConfigurationBaseManager getInstance() {
    return ServiceManager.getService(PhpStanConfigurationBaseManager.class);
  }
}

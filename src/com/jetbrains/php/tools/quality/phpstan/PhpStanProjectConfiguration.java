package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.jetbrains.php.tools.quality.QualityToolProjectConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurationBaseManager.PHP_STAN;

@State(name = "PhpStanProjectConfiguration", storages = {@Storage(StoragePathMacros.WORKSPACE_FILE)})
public class PhpStanProjectConfiguration extends QualityToolProjectConfiguration<PhpStanConfiguration>
  implements PersistentStateComponent<PhpStanProjectConfiguration> {

  @Nullable
  @Override
  public PhpStanProjectConfiguration getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull PhpStanProjectConfiguration state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  @Override
  protected String getInspectionId() {
    return new PhpStanValidationInspection().getID();
  }

  @NotNull
  @Override
  protected String getQualityToolName() {
    return PHP_STAN;
  }

  @NotNull
  @Override
  protected PhpStanConfigurationManager getConfigurationManager(@NotNull Project project) {
    return PhpStanConfigurationManager.getInstance(project);
  }

  public static PhpStanProjectConfiguration getInstance(Project project) {
    return ServiceManager.getService(project, PhpStanProjectConfiguration.class);
  }
}

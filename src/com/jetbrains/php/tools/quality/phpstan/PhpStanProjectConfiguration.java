package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.jetbrains.php.tools.quality.QualityToolProjectConfiguration;
import com.jetbrains.php.tools.quality.QualityToolType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "PhpStanProjectConfiguration", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
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
  protected QualityToolType<PhpStanConfiguration> getQualityToolType() {
    return PhpStanQualityToolType.INSTANCE;
  }

  public static PhpStanProjectConfiguration getInstance(Project project) {
    return ServiceManager.getService(project, PhpStanProjectConfiguration.class);
  }
}

package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.jetbrains.php.tools.quality.QualityToolProjectConfiguration;
import com.jetbrains.php.tools.quality.QualityToolType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "PhpStanProjectConfiguration", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class PhpStanProjectConfiguration extends QualityToolProjectConfiguration<PhpStanConfiguration>
  implements PersistentStateComponent<PhpStanProjectConfiguration> {

  @Override
  public @Nullable PhpStanProjectConfiguration getState() {
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
    return project.getService(PhpStanProjectConfiguration.class);
  }
}

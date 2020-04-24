package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurationBaseManager.PHP_STAN;

public class PhpStanQualityToolType extends QualityToolType<PhpStanConfiguration> {
  @NotNull
  @Override
  public String getDisplayName() {
    return PHP_STAN;
  }

  @Override
  public @NotNull QualityToolBlackList getQualityToolBlackList(@NotNull Project project) {
    return PhpStanBlackList.getInstance(project);
  }

  @Override
  protected @NotNull QualityToolConfigurableList<PhpStanConfiguration> getQualityToolConfigurableList(@NotNull Project project,
                                                                                                      @Nullable String item) {
    return new PhpStanConfigurableList(project, item);
  }

  @Override
  protected @NotNull QualityToolConfigurationManager<PhpStanConfiguration> getConfigurationManager(@NotNull Project project) {
    return PhpStanConfigurationManager.getInstance(project);
  }

  @Override
  protected @NotNull QualityToolValidationInspection getInspection() {
    return new PhpStanValidationInspection();
  }
}

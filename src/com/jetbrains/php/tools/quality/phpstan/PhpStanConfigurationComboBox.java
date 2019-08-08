package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolConfigurableList;
import com.jetbrains.php.tools.quality.QualityToolConfigurationComboBox;
import com.jetbrains.php.tools.quality.QualityToolConfigurationManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhpStanConfigurationComboBox extends QualityToolConfigurationComboBox<PhpStanConfiguration> {

  public PhpStanConfigurationComboBox(@Nullable Project project) {
    super(project);
  }

  @Override
  protected QualityToolConfigurableList<PhpStanConfiguration> getQualityToolConfigurableList(@NotNull Project project,
                                                                                             @Nullable String item) {
    return new PhpStanConfigurableList(project, item);
  }

  @Override
  protected QualityToolConfigurationManager<PhpStanConfiguration> getConfigurationManager(@NotNull Project project) {
    return PhpStanConfigurationManager.getInstance(project);
  }
}

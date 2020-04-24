package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolProjectConfigurableForm;
import com.jetbrains.php.tools.quality.QualityToolProjectConfiguration;
import com.jetbrains.php.tools.quality.QualityToolType;
import org.jetbrains.annotations.NotNull;

public class PhpStanConfigurable extends QualityToolProjectConfigurableForm implements Configurable.NoScroll {

  public PhpStanConfigurable(@NotNull Project project) {
    super(project);
  }

  @Override
  protected QualityToolProjectConfiguration getProjectConfiguration() {
    return PhpStanProjectConfiguration.getInstance(myProject);
  }

  @Override
  public String getHelpTopic() {
    return "reference.settings.php.PHPStan";
  }

  @Override
  protected QualityToolType getQualityToolType() {
    return new PhpStanQualityToolType();
  }
}

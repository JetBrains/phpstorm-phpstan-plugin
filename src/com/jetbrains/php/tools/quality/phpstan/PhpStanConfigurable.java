package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolConfigurationComboBox;
import com.jetbrains.php.tools.quality.QualityToolProjectConfigurableForm;
import com.jetbrains.php.tools.quality.QualityToolProjectConfiguration;
import com.jetbrains.php.tools.quality.QualityToolType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class PhpStanConfigurable extends QualityToolProjectConfigurableForm implements Configurable.NoScroll {

  public PhpStanConfigurable(@NotNull Project project) {
    super(project);
  }

  @Override
  protected QualityToolProjectConfiguration getProjectConfiguration() {
    return PhpStanProjectConfiguration.getInstance(myProject);
  }

  @Nls
  @Override
  public String getDisplayName() {
    return PhpStanBundle.message("configurable.PhpStanConfigurable.display.name");
  }

  @Override
  public String getHelpTopic() {
    return "reference.settings.php.PHPStan";
  }

  @NotNull
  @Override
  public String getId() {
    return PhpStanConfigurable.class.getName();
  }

  @NotNull
  @Override
  protected String getInspectionShortName() {
    return new PhpStanValidationInspection().getShortName();
  }

  @NotNull
  @Override
  protected QualityToolConfigurationComboBox createConfigurationComboBox() {
    return new PhpStanConfigurationComboBox(myProject);
  }

  @Override
  protected QualityToolType getQualityToolType() {
    return new PhpStanQualityToolType();
  }
}

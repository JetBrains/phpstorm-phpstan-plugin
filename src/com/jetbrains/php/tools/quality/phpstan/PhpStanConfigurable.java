package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.jetbrains.php.tools.quality.QualityToolConfigurationComboBox;
import com.jetbrains.php.tools.quality.QualityToolProjectConfigurableForm;
import com.jetbrains.php.tools.quality.QualityToolValidationException;
import com.jetbrains.php.tools.quality.QualityToolsIgnoreFilesConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhpStanConfigurable extends QualityToolProjectConfigurableForm implements Configurable.NoScroll {

  public PhpStanConfigurable(@NotNull Project project) {
    super(project);
  }

  @Nls
  @Override
  public String getDisplayName() {
    return "PHPStan";
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

  @Override
  protected void updateSelectedConfiguration(@Nullable String newConfigurationId) {
    final PhpStanProjectConfiguration projectConfiguration = PhpStanProjectConfiguration.getInstance(myProject);
    if (newConfigurationId != null && !StringUtil.equals(newConfigurationId, projectConfiguration.getSelectedConfigurationId())) {
      projectConfiguration.setSelectedConfigurationId(newConfigurationId);
    }
  }

  @Nullable
  @Override
  protected String getSavedSelectedConfigurationId() {
    return PhpStanProjectConfiguration.getInstance(myProject).getSelectedConfigurationId();
  }

  @Nullable
  @Override
  protected String validate(@Nullable String configuration) {
    try {
      PhpStanProjectConfiguration.getInstance(myProject).findConfiguration(myProject, configuration);
      return null;
    }
    catch (QualityToolValidationException e) {
      return e.getMessage();
    }
  }

  @NotNull
  @Override
  protected QualityToolConfigurationComboBox createConfigurationComboBox() {
    return new PhpStanConfigurationComboBox(myProject);
  }

  @Override
  protected QualityToolsIgnoreFilesConfigurable getIgnoredFilesConfigurable() {
    return new PhpStanIgnoredFilesConfigurable(myProject);
  }
}

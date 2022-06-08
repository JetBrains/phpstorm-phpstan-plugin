package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolProjectConfigurableForm;
import com.jetbrains.php.tools.quality.QualityToolType;
import com.jetbrains.php.tools.quality.QualityToolsOptionsPanel;
import org.jetbrains.annotations.NotNull;

public class PhpStanConfigurable extends QualityToolProjectConfigurableForm implements Configurable.NoScroll {

  public PhpStanConfigurable(@NotNull Project project) {
    super(project);
  }

  @Override
  protected QualityToolsOptionsPanel getQualityToolOptionPanel() {
    return new PhpStanOptionsPanel(new PhpStanGlobalInspection());
  }

  @Override
  protected QualityToolType<PhpStanConfiguration> getQualityToolType() {
    return PhpStanQualityToolType.INSTANCE;
  }
}

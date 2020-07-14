package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.composer.actions.log.ComposerLogMessageBuilder;
import com.jetbrains.php.tools.quality.QualityToolConfigurableList;
import com.jetbrains.php.tools.quality.QualityToolType;
import com.jetbrains.php.ui.PhpUiUtil;
import org.jetbrains.annotations.NotNull;

public class PhpStanOpenSettingsProvider extends ComposerLogMessageBuilder.Settings {
  public static final PhpStanOpenSettingsProvider PHP_STAN_OPEN_SETTINGS_PROVIDER = new PhpStanOpenSettingsProvider();

  public PhpStanOpenSettingsProvider() {super("\u200D");}

  @Override
  public void show(@NotNull Project project) {
    PhpUiUtil.editConfigurable(project, new QualityToolConfigurableList<PhpStanConfiguration>(project, PhpStanQualityToolType.INSTANCE, null) {
      @Override
      protected QualityToolType<PhpStanConfiguration> getQualityToolType() {
        return PhpStanQualityToolType.INSTANCE;
      }
    });
  }
}
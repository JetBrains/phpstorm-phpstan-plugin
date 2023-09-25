package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.composer.actions.log.ComposerLogMessageBuilder;
import org.jetbrains.annotations.NotNull;

public class PhpStanOpenSettingsProvider extends ComposerLogMessageBuilder.Settings {
  public static final PhpStanOpenSettingsProvider PHP_STAN_OPEN_SETTINGS_PROVIDER = new PhpStanOpenSettingsProvider();

  public PhpStanOpenSettingsProvider() {super("\u200D");}

  @Override
  public void show(@NotNull Project project) {
    ShowSettingsUtil.getInstance().showSettingsDialog(project, PhpStanBundle.message("configurable.quality.tool.phpstan"));
  }
}
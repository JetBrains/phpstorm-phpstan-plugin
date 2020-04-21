package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolAddToIgnoredAction;
import com.jetbrains.php.tools.quality.QualityToolBlackList;
import com.jetbrains.php.tools.quality.QualityToolProjectConfigurableForm;
import com.jetbrains.php.tools.quality.QualityToolsIgnoreFilesConfigurable;
import org.jetbrains.annotations.NotNull;

import static com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurationBaseManager.PHP_STAN;

public class PhpStanAddToIgnoredAction extends QualityToolAddToIgnoredAction {


  @NotNull
  @Override
  protected QualityToolBlackList getBlackList(Project project) {
    return PhpStanBlackList.getInstance(project);
  }

  @NotNull
  @Override
  protected String getToolName() {
    return PHP_STAN;
  }

  @NotNull
  @Override
  protected QualityToolProjectConfigurableForm getToolConfigurable(@NotNull Project project) {
    return new PhpStanConfigurable(project);
  }

  @Override
  protected QualityToolsIgnoreFilesConfigurable getIgnoredFilesConfigurable(@NotNull Project project) {
    return new PhpStanIgnoredFilesConfigurable(project);
  }
}

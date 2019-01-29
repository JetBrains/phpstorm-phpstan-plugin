package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolsIgnoreFilesConfigurable;
import org.jetbrains.annotations.NotNull;

public class PhpStanIgnoredFilesConfigurable extends QualityToolsIgnoreFilesConfigurable {
  public PhpStanIgnoredFilesConfigurable(Project project) {super(PhpStanBlackList.getInstance(project), project);}

  @NotNull
  @Override
  public String getId() {
    return PhpStanIgnoredFilesConfigurable.class.getName();
  }

  @NotNull
  @Override
  protected String getQualityToolName() {
    return "PHPStan";
  }
}

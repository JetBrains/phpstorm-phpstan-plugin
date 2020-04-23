package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolAddToIgnoredAction;
import com.jetbrains.php.tools.quality.QualityToolBlackList;
import org.jetbrains.annotations.NotNull;

public class PhpStanAddToIgnoredAction extends QualityToolAddToIgnoredAction {

  @NotNull
  @Override
  protected QualityToolBlackList getBlackList(Project project) {
    return PhpStanBlackList.getInstance(project);
  }
}

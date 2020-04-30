package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolAddToIgnoredAction;
import com.jetbrains.php.tools.quality.QualityToolType;
import org.jetbrains.annotations.NotNull;

public class PhpStanAddToIgnoredAction extends QualityToolAddToIgnoredAction {

  @Override
  protected @NotNull QualityToolType<PhpStanConfiguration> getQualityToolType(Project project) {
    return PhpStanQualityToolType.INSTANCE;
  }
}

package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.codeInspection.LocalInspectionTool;
import com.jetbrains.php.tools.quality.QualityToolAnnotator;
import com.jetbrains.php.tools.quality.QualityToolValidationGlobalInspection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhpStanGlobalInspection extends QualityToolValidationGlobalInspection {


  @Override
  public @Nullable LocalInspectionTool getSharedLocalInspectionTool() {
    return new PhpStanValidationInspection();
  }

  @Override
  protected @NotNull QualityToolAnnotator getAnnotator() {
    return PhpStanAnnotatorProxy.INSTANCE;
  }
}

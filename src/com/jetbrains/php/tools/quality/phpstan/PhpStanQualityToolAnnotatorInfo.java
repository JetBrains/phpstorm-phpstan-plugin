package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.codeInspection.InspectionProfile;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.tools.quality.QualityToolAnnotatorInfo;
import com.jetbrains.php.tools.quality.QualityToolConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhpStanQualityToolAnnotatorInfo extends QualityToolAnnotatorInfo<PhpStanValidationInspection> {

  public PhpStanQualityToolAnnotatorInfo(@Nullable PsiFile psiFile,
                                         @NotNull PhpStanValidationInspection inspection,
                                         @NotNull InspectionProfile profile,
                                         @NotNull Project project,
                                         @NotNull QualityToolConfiguration configuration, boolean isOnTheFly) {
    super(psiFile, inspection, profile, project, configuration, isOnTheFly);
  }
}

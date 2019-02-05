package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.tools.quality.QualityToolAnnotatorInfo;
import com.jetbrains.php.tools.quality.QualityToolConfiguration;
import com.jetbrains.php.tools.quality.QualityToolValidationInspection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PhpStanQualityToolAnnotatorInfo extends QualityToolAnnotatorInfo {
  private List<String> dependsOn = new ArrayList<>();

  public PhpStanQualityToolAnnotatorInfo(@NotNull PsiFile psiFile,
                                         @NotNull QualityToolValidationInspection inspection,
                                         @NotNull Project project,
                                         @NotNull QualityToolConfiguration configuration, boolean isOnTheFly) {
    super(psiFile, inspection, project, configuration, isOnTheFly);
  }


  public List<String> getDependsOn() {
    return dependsOn;
  }

  public void setDependsOn(List<String> dependsOn) {
    this.dependsOn = dependsOn;
  }
}

package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.codeInspection.InspectionProfile;
import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.codeInspection.ex.InspectionToolWrapper;
import com.intellij.openapi.project.Project;
import com.intellij.profile.codeInspection.InspectionProjectProfileManager;
import com.jetbrains.php.tools.quality.QualityToolAnnotator;
import com.jetbrains.php.tools.quality.QualityToolValidationInspection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.util.ObjectUtils.tryCast;
import static com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurationBaseManager.PHP_STAN;

@SuppressWarnings("InspectionDescriptionNotFoundInspection")
public class PhpStanValidationInspection extends QualityToolValidationInspection {

  @NotNull
  @Override
  protected QualityToolAnnotator getAnnotator() {
    return PhpStanAnnotatorProxy.INSTANCE;
  }

  @Override
  public String getToolName() {
    return PHP_STAN;
  }

  @Nullable
  public PhpStanGlobalInspection getGlobalTool(@NotNull Project project) {
    final InspectionToolWrapper inspectionTool = ((InspectionProfile)InspectionProjectProfileManager.getInstance(project)
      .getCurrentProfile())
      .getInspectionTool("PhpStanGlobalInspection", project);
    if (inspectionTool == null){
      return null;
    }
    return tryCast(inspectionTool.getTool(), PhpStanGlobalInspection.class);
  }
}

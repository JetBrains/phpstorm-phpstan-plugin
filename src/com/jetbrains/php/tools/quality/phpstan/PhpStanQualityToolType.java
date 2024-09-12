package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.codeInspection.InspectionProfile;
import com.intellij.codeInspection.ex.InspectionToolWrapper;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.profile.codeInspection.InspectionProjectProfileManager;
import com.jetbrains.php.tools.quality.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.util.ObjectUtils.tryCast;
import static com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurationBaseManager.PHP_STAN;

public final class PhpStanQualityToolType extends QualityToolType<PhpStanConfiguration> {
  public static final PhpStanQualityToolType INSTANCE = new PhpStanQualityToolType();

  private PhpStanQualityToolType() {
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return PHP_STAN;
  }

  @Override
  public @NotNull QualityToolBlackList getQualityToolBlackList(@NotNull Project project) {
    return PhpStanBlackList.getInstance(project);
  }

  @Override
  protected @NotNull QualityToolConfigurationManager<PhpStanConfiguration> getConfigurationManager(@NotNull Project project) {
    return PhpStanConfigurationManager.getInstance(project);
  }

  @Override
  protected @NotNull PhpStanValidationInspection getInspection() {
    return new PhpStanValidationInspection();
  }

  @Override
  protected @Nullable QualityToolConfigurationProvider<PhpStanConfiguration> getConfigurationProvider() {
    return PhpStanConfigurationProvider.getInstances();
  }

  @Override
  protected @NotNull QualityToolConfigurableForm<PhpStanConfiguration> createConfigurableForm(@NotNull Project project,
                                                                                              PhpStanConfiguration settings) {
    return new PhpStanConfigurableForm<>(project, settings);
  }

  @Override
  protected @NotNull Configurable getToolConfigurable(@NotNull Project project) {
    return new PhpStanConfigurable(project);
  }

  @Override
  protected @NotNull QualityToolProjectConfiguration<PhpStanConfiguration> getProjectConfiguration(@NotNull Project project) {
    return PhpStanProjectConfiguration.getInstance(project);
  }

  @NotNull
  @Override
  protected PhpStanConfiguration createConfiguration() {
    return new PhpStanConfiguration();
  }

  @Override
  public @NotNull String getHelpTopic() {
    return "reference.settings.php.PHPStan";
  }

  @Override
  public QualityToolValidationGlobalInspection getGlobalTool(@NotNull Project project,
                                                             @Nullable InspectionProfile profile) {
    if (profile == null) {
      profile = InspectionProjectProfileManager.getInstance(project).getCurrentProfile();
    }
    final InspectionToolWrapper<?, ?> inspectionTool = profile.getInspectionTool(getInspectionId(), project);
    if (inspectionTool == null) {
      return null;
    }
    return tryCast(inspectionTool.getTool(), PhpStanGlobalInspection.class);
  }

  @Override
  public String getInspectionId() {
    return "PhpStanGlobal";
  }
  
  @Override
  public String getInspectionShortName(@NotNull Project project) {
    final QualityToolValidationGlobalInspection tool = getGlobalTool(project, null);
    if (tool != null) {
      return tool.getShortName();
    }
    return getInspection().getShortName();
  }
}

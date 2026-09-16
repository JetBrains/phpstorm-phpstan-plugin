package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolBlackList;
import com.jetbrains.php.tools.quality.QualityToolConfigurableForm;
import com.jetbrains.php.tools.quality.QualityToolConfigurationManager;
import com.jetbrains.php.tools.quality.QualityToolConfigurationProvider;
import com.jetbrains.php.tools.quality.QualityToolProjectConfiguration;
import com.jetbrains.php.tools.quality.QualityToolType;
import com.jetbrains.php.tools.quality.QualityToolValidationGlobalInspection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurationBaseManager.PHP_STAN;

public final class PhpStanQualityToolType extends QualityToolType<PhpStanConfiguration> {
  public static final PhpStanQualityToolType INSTANCE = new PhpStanQualityToolType();

  private PhpStanQualityToolType() {
  }

  @Override
  public @NotNull String getDisplayName() {
    return PHP_STAN;
  }

  @Override
  public @NotNull QualityToolBlackList getQualityToolBlackList(@NotNull Project project) {
    return PhpStanBlackList.getInstance(project);
  }

  @Override
  public @NotNull QualityToolConfigurationManager<PhpStanConfiguration> getConfigurationManager(@NotNull Project project) {
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

  @Override
  protected @NotNull PhpStanConfiguration createConfiguration() {
    return new PhpStanConfiguration();
  }

  @Override
  public @NotNull String getHelpTopic() {
    return "reference.settings.php.PHPStan";
  }

  @Override
  protected @NotNull Class<? extends QualityToolValidationGlobalInspection> getGlobalInspectionClass() {
    return PhpStanGlobalInspection.class;
  }

  @Override
  public String getInspectionId() {
    return "PhpStanGlobal";
  }
}

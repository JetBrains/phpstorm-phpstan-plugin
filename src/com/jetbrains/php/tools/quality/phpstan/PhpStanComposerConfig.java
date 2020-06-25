package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.php.composer.ComposerOpenSettingsProvider;
import com.jetbrains.php.composer.actions.log.ComposerLogMessageBuilder;
import com.jetbrains.php.tools.quality.QualityToolConfigurableList;
import com.jetbrains.php.tools.quality.QualityToolConfigurationManager;
import com.jetbrains.php.tools.quality.QualityToolType;
import com.jetbrains.php.tools.quality.QualityToolsComposerConfig;
import com.jetbrains.php.ui.PhpUiUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class PhpStanComposerConfig extends QualityToolsComposerConfig<PhpStanConfiguration, PhpStanValidationInspection> implements
                                                                                                                         ComposerOpenSettingsProvider {
  @NonNls private static final String PACKAGE = "phpstan/phpstan";
  @NonNls private static final String RELATIVE_PATH = "bin/phpstan" + (SystemInfo.isWindows ? ".bat" : "");
  @NonNls private static final String PHPSTAN_NEON = "phpstan.neon";
  private static final PhpStanValidationInspection PHP_STAN_VALIDATION_INSPECTION = new PhpStanValidationInspection();


  public PhpStanComposerConfig() {
    super(PACKAGE, RELATIVE_PATH);
  }

  @Override
  protected ComposerLogMessageBuilder.Settings getQualityToolsInspectionSettings() {
    return null;
  }

  @Override
  public PhpStanValidationInspection getQualityInspection() {
    return PHP_STAN_VALIDATION_INSPECTION;
  }
  
  @Override
  protected boolean applyRulesetFromComposer(@NotNull Project project, PhpStanConfiguration configuration) {
    return false;
  }

  @Override
  protected boolean applyRulesetFromRoot(@NotNull Project project) {
    VirtualFile customRulesetFile = detectCustomRulesetFile(project.getBaseDir(), PHPSTAN_NEON);
    if(customRulesetFile == null){
      customRulesetFile = detectCustomRulesetFile(project.getBaseDir(), PHPSTAN_NEON + ".dist");
    }

    if (customRulesetFile != null) {
      final String path = customRulesetFile.getPath();
      return modifyRulesetInspectionSetting(project, tool -> applyRuleset(tool, path));
    }
    return false;
  }

  @Override
  public void openSettings(@NotNull Project project) {
    PhpUiUtil.editConfigurable(project, new QualityToolConfigurableList<PhpStanConfiguration>(project, PhpStanQualityToolType.INSTANCE, null) {
      @Override
      protected QualityToolType<PhpStanConfiguration> getQualityToolType() {
        return PhpStanQualityToolType.INSTANCE;
      }
    });
  }


  private static void applyRuleset(PhpStanValidationInspection tool, String customRuleset) {
    tool.config = customRuleset;
  }

  @NotNull
  @Override
  protected QualityToolConfigurationManager<PhpStanConfiguration> getConfigurationManager(@NotNull Project project) {
    return PhpStanConfigurationManager.getInstance(project);
  }
}
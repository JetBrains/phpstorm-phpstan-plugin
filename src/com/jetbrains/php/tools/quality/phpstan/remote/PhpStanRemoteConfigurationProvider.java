package com.jetbrains.php.tools.quality.phpstan.remote;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.NullableFunction;
import com.intellij.util.xmlb.XmlSerializer;
import com.jetbrains.php.config.interpreters.PhpInterpreter;
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl;
import com.jetbrains.php.config.interpreters.PhpSdkAdditionalData;
import com.jetbrains.php.remote.interpreter.PhpRemoteSdkAdditionalData;
import com.jetbrains.php.remote.tools.quality.QualityToolByInterpreterConfigurableForm;
import com.jetbrains.php.remote.tools.quality.QualityToolByInterpreterDialog;
import com.jetbrains.php.tools.quality.QualityToolConfigurableForm;
import com.jetbrains.php.tools.quality.phpstan.*;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.intellij.openapi.util.text.StringUtil.isNotEmpty;
import static com.jetbrains.php.remote.tools.quality.QualityToolByInterpreterDialog.getLocalOrDefaultInterpreterConfiguration;
import static com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurationBaseManager.PHP_STAN;

public class PhpStanRemoteConfigurationProvider extends PhpStanConfigurationProvider {

  private static final @NonNls String PHPSTAN_BY_INTERPRETER = "phpstan_by_interpreter";

  @Override
  public boolean canLoad(@NotNull String tagName) {
    return StringUtil.equals(tagName, PHPSTAN_BY_INTERPRETER);
  }

  @Override
  public @Nullable PhpStanConfiguration load(@NotNull Element element) {
    return XmlSerializer.deserialize(element, PhpStanRemoteConfiguration.class);
  }

  @Override
  public @Nullable QualityToolConfigurableForm<PhpStanRemoteConfiguration> createConfigurationForm(@NotNull Project project,
                                                                                                   @NotNull PhpStanConfiguration settings) {
    if (settings instanceof PhpStanRemoteConfiguration remoteConfiguration) {
      final PhpStanConfigurableForm<PhpStanRemoteConfiguration> delegate =
        new PhpStanConfigurableForm<>(project, remoteConfiguration);
      return new QualityToolByInterpreterConfigurableForm<>(project, remoteConfiguration, delegate);
    }
    return null;
  }

  @Override
  public PhpStanConfiguration createNewInstance(@Nullable Project project, @NotNull List<PhpStanConfiguration> existingSettings) {
    var dialog =
      new QualityToolByInterpreterDialog<>(project, existingSettings, PHP_STAN, PhpStanConfiguration.class, PhpStanQualityToolType.INSTANCE);
    if (dialog.showAndGet()) {
      final String id = PhpInterpretersManagerImpl.getInstance(project).findInterpreterId(dialog.getSelectedInterpreterName());
      if (isNotEmpty(id)) {
        final PhpStanRemoteConfiguration settings = new PhpStanRemoteConfiguration();
        settings.setInterpreterId(id);

        final PhpSdkAdditionalData data = PhpInterpretersManagerImpl.getInstance(project).findInterpreterDataById(id);
        fillDefaultSettings(project, settings, PhpStanConfigurationManager.getInstance(project).getOrCreateLocalSettings(), data, data instanceof PhpRemoteSdkAdditionalData);

        return settings;
      }
      return (PhpStanConfiguration)getLocalOrDefaultInterpreterConfiguration(dialog.getSelectedInterpreterName(), project, PhpStanQualityToolType.INSTANCE);
    }
    return null;
  }

  @Override
  public PhpStanConfiguration createConfigurationByInterpreter(@NotNull PhpInterpreter interpreter) {
    final PhpStanRemoteConfiguration settings = new PhpStanRemoteConfiguration();
    settings.setInterpreterId(interpreter.getId());
    return settings;
  }

  @Override
  protected void fillSettingsByDefaultValue(@NotNull PhpStanConfiguration settings,
                                            @NotNull PhpStanConfiguration localConfiguration,
                                            @NotNull NullableFunction<String, String> preparePath) {
    super.fillSettingsByDefaultValue(settings, localConfiguration, preparePath);
    settings.setTimeout(60000);
  }
}

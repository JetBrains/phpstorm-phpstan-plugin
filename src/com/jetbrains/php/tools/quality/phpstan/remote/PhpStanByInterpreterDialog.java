package com.jetbrains.php.tools.quality.phpstan.remote;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.remote.tools.quality.QualityToolByInterpreterDialog;
import com.jetbrains.php.tools.quality.phpstan.PhpStanConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class PhpStanByInterpreterDialog extends QualityToolByInterpreterDialog<PhpStanConfiguration, PhpStanRemoteConfiguration> {
  protected PhpStanByInterpreterDialog(@Nullable Project project, @NotNull List<PhpStanConfiguration> settings) {
    super(project, settings, "PHP  Stan");
  }

  @Override
  protected boolean canProcessSetting(@NotNull PhpStanConfiguration settings) {
    return settings instanceof PhpStanRemoteConfiguration;
  }

  @Nullable
  @Override
  protected String getInterpreterId(@NotNull PhpStanRemoteConfiguration configuration) {
    return configuration.getInterpreterId();
  }
}

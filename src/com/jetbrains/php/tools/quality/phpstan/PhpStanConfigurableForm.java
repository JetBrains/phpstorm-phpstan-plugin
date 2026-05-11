package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Version;
import com.jetbrains.php.PhpBundle;
import com.jetbrains.php.tools.quality.QualityToolConfigurableForm;
import com.jetbrains.php.tools.quality.QualityToolType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurationBaseManager.PHP_STAN;

public class PhpStanConfigurableForm<C extends PhpStanConfiguration> extends QualityToolConfigurableForm<C> {

  private final C myPhpStanConfiguration;

  public PhpStanConfigurableForm(@NotNull Project project, @NotNull C configuration) {
    super(project, configuration, PHP_STAN, "phpstan");
    myPhpStanConfiguration = configuration;
  }

  @Override
  public QualityToolType getQualityToolType() {
    return PhpStanQualityToolType.INSTANCE;
  }

  @Override
  public String getHelpTopic() {
    return "reference.settings.php.PhpStan";
  }

  @Override
  public @NotNull Pair<Boolean, String> validateMessage(@NonNls String message) {
    final String versionString = PhpStanVersionSupport.extractVersionFromOutput(message);
    final Version version = versionString != null ? extractVersion(versionString) : null;
    if (version == null || !message.contains(PHP_STAN)) {
      // Clear version on validation failure
      myPhpStanConfiguration.setVersion(null);
      return Pair.create(false, PhpBundle.message("quality.tool.can.not.determine.version", message));
    }
    // Store the version for editor mode support detection
    myPhpStanConfiguration.setVersion(versionString);
    return Pair.create(true, "OK, " + message);
  }
}
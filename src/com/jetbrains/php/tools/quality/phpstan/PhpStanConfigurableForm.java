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

  public PhpStanConfigurableForm(@NotNull Project project, @NotNull C configuration) {
    super(project, configuration, PHP_STAN, "phpstan");
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
    final Version version = extractVersion(message.trim().replaceFirst("PHPStan.* ([\\d.]*).*", "$1").trim());
    if (version == null || !message.contains(PHP_STAN)) {
      return Pair.create(false, PhpBundle.message("quality.tool.can.not.determine.version", message));
    }
    return Pair.create(true, "OK, " + message);
  }
}
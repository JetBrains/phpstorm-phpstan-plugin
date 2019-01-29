package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Version;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.php.PhpBundle;
import com.jetbrains.php.tools.quality.QualityToolConfigurableForm;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class PhpStanConfigurableForm<C extends PhpStanConfiguration> extends QualityToolConfigurableForm<C> {

  public PhpStanConfigurableForm(@NotNull Project project, @NotNull C configuration) {
    super(project, configuration, "PHPStan", "phpstan");
  }

  @Nls
  @Override
  public String getDisplayName() {
    return "PHPStan";
  }

  @Override
  public String getHelpTopic() {
    return "reference.settings.php.PhpStan";
  }

  @NotNull
  @Override
  public String getId() {
    return PhpStanConfigurableForm.class.getName();
  }

  @NotNull
  @Override
  public Pair<Boolean, String> validateMessage(String message) {
    final Version version = extractVersion(message.replaceFirst("PHPStan.* ([\\d.]*).*", "$1").trim());
    if (version == null || !message.contains("PHPStan")) {
      return Pair.create(false, PhpBundle.message("quality.tool.can.not.determine.version", message));
    }
    return Pair.create(true, "OK, " + message);
  }

  @Override
  public boolean isValidToolFile(VirtualFile file) {
    return file.getName().startsWith("phpstan");
  }
}
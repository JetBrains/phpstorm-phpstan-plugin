package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolConfigurationManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhpStanConfigurationManager extends QualityToolConfigurationManager<PhpStanConfiguration> {

  public static final int DEFAULT_MAX_MESSAGES_PER_FILE = 50;

  public PhpStanConfigurationManager(@Nullable Project project) {
    super(project);
    if (project != null) {
      myProjectManager = project.getService(PhpStanProjectConfigurationManager.class);
    }
    myApplicationManager = ApplicationManager.getApplication().getService(PhpStanAppConfigurationManager.class);
  }

  public static PhpStanConfigurationManager getInstance(@NotNull Project project) {
    return project.getService(PhpStanConfigurationManager.class);
  }

  @State(name = "PhpStan", storages = @Storage("php.xml"))
  static class PhpStanProjectConfigurationManager extends PhpStanConfigurationBaseManager {
  }

  @State(name = "PhpStan", storages = @Storage("php.xml"))
  static class PhpStanAppConfigurationManager extends PhpStanConfigurationBaseManager {
  }
}

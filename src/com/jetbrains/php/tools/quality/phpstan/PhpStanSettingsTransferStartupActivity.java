package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.profile.codeInspection.InspectionProfileManager;
import org.jetbrains.annotations.NotNull;

public class PhpStanSettingsTransferStartupActivity implements StartupActivity {
  @Override
  public void runActivity(@NotNull Project project) {
    if (project.isDefault()) {
      return;
    }
    Application app = ApplicationManager.getApplication();
    if (app.isUnitTestMode() || app.isHeadlessEnvironment()) {
      return;
    }

    PhpStanGlobalInspection tool =
      (PhpStanGlobalInspection)PhpStanQualityToolType.INSTANCE.getGlobalTool(project,
                                                                         InspectionProfileManager.getInstance().getCurrentProfile());
    PhpStanProjectConfiguration instance = PhpStanProjectConfiguration.getInstance(project);
    if (tool != null && !tool.transferred) {
      instance.setConfig(tool.config);
      instance.setAutoload(tool.autoload);
      instance.setLevel(tool.level);
      instance.setMemoryLimit(tool.memoryLimit);
      tool.transferred = true;
    }
  }
}

package com.jetbrains.php.tools.quality.phpstan

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.profile.codeInspection.InspectionProfileManager
import com.intellij.util.PlatformUtils

private class PhpStanSettingsTransferStartupActivity : ProjectActivity {
  override suspend fun execute(project: Project) {
    if (project.isDefault) return
    val app = ApplicationManager.getApplication()
    if (app.isUnitTestMode || app.isHeadlessEnvironment || !PlatformUtils.isPhpStorm()) return

    val tool = PhpStanQualityToolType.INSTANCE.getGlobalTool(project, InspectionProfileManager.getInstance(project).currentProfile) as? PhpStanGlobalInspection
    val instance = PhpStanOptionsConfiguration.getInstance(project)
    tool?.let {
      if (!instance.isTransferred) {
        instance.config = tool.config
        instance.autoload = tool.autoload
        instance.level = tool.level
        instance.memoryLimit = tool.memoryLimit
        instance.isFullProject = tool.FULL_PROJECT
        instance.isTransferred = true
      }
    }
  }
}

package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolBlackList;

@State(name = "PhpStanBlackList", storages = {@Storage(StoragePathMacros.WORKSPACE_FILE)})
public class PhpStanBlackList extends QualityToolBlackList {

  public static PhpStanBlackList getInstance(Project project) {
    return ServiceManager.getService(project, PhpStanBlackList.class);
  }
}

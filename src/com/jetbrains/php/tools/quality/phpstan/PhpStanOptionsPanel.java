package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PhpStanOptionsPanel {
  private final PhpStanValidationInspection myInspection;
  private JPanel myOptionsPanel;

  public PhpStanOptionsPanel(PhpStanValidationInspection inspection) {
    myInspection = inspection;
  }

  private void createUIComponents() {
  }

  public JPanel getOptionsPanel() {
    return myOptionsPanel;
  }

  @NotNull
  private Project getCurrentProject() {
    Project project = CommonDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext(myOptionsPanel));
    if (project == null) {
      project = ProjectManager.getInstance().getDefaultProject();
    }
    return project;
  }

  public void init() {
  }
}

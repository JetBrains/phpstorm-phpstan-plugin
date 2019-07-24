package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

public class PhpStanOptionsPanel {
  private final PhpStanValidationInspection myInspection;
  private JPanel myOptionsPanel;
  private JBCheckBox myFullProjectRunJBCheckBox;
  private JBTextField myOptionsTextField;

  public PhpStanOptionsPanel(PhpStanValidationInspection inspection) {
    myInspection = inspection;
    myFullProjectRunJBCheckBox.setSelected(inspection.FULL_PROJECT);
    myFullProjectRunJBCheckBox.addActionListener(event -> myInspection.FULL_PROJECT = myFullProjectRunJBCheckBox.isSelected());

    myOptionsTextField.setText(inspection.OPTIONS);
    myOptionsTextField.getDocument().addDocumentListener(new DocumentAdapter() {
      @Override
      protected void textChanged(@NotNull DocumentEvent e) {
        inspection.OPTIONS = myOptionsTextField.getText();
      }
    });
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

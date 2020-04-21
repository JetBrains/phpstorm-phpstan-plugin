package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.jetbrains.php.config.interpreters.PhpInterpreter;
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl;
import com.jetbrains.php.config.interpreters.PhpSdkAdditionalData;
import com.jetbrains.php.config.interpreters.PhpTextFieldWithSdkBasedBrowse;
import com.jetbrains.php.tools.quality.QualityToolValidationException;
import com.jetbrains.php.tools.quality.phpcs.PhpCSConfiguration;
import com.jetbrains.php.tools.quality.phpcs.PhpCSProjectConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

public class PhpStanOptionsPanel {
  private final PhpStanValidationInspection myInspection;
  private JPanel myOptionsPanel;
  private JBCheckBox myFullProjectRunJBCheckBox;
  private JBTextField myMemoryLimitTextField;
  private JBIntSpinner myJBIntSpinner;
  private PhpTextFieldWithSdkBasedBrowse myConfigPathTextField;
  private PhpTextFieldWithSdkBasedBrowse myAutoloadPathTextField;

  public PhpStanOptionsPanel(PhpStanValidationInspection inspection) {
    myInspection = inspection;
    myFullProjectRunJBCheckBox.setSelected(inspection.FULL_PROJECT);
    myFullProjectRunJBCheckBox.addActionListener(event -> myInspection.FULL_PROJECT = myFullProjectRunJBCheckBox.isSelected());

    myMemoryLimitTextField.setText(inspection.memoryLimit);
    myMemoryLimitTextField.getDocument().addDocumentListener(new DocumentAdapter() {
      @Override
      protected void textChanged(@NotNull DocumentEvent e) {
        inspection.memoryLimit = myMemoryLimitTextField.getText();
      }
    });
    myJBIntSpinner.setNumber(inspection.level);
    myJBIntSpinner.addChangeListener(event -> myInspection.level = myJBIntSpinner.getNumber());
    final Project project = getCurrentProject();
    myConfigPathTextField.setText(inspection.config);
    myConfigPathTextField
      .init(project, getSdkAdditionalData(project), PhpStanBundle.message("phpstan.configuration.file"), true, false);
    myConfigPathTextField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
      @Override
      protected void textChanged(@NotNull DocumentEvent e) {
        myInspection.config = myConfigPathTextField.getText();
      }
    });
    
    myAutoloadPathTextField.setText(inspection.autoload);
    myAutoloadPathTextField
      .init(project, getSdkAdditionalData(project), PhpStanBundle.message("phpstan.autoload.file"), true, false);
    myAutoloadPathTextField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
      @Override
      protected void textChanged(@NotNull DocumentEvent e) {
        myInspection.autoload = myAutoloadPathTextField.getText();
      }
    });
  }

  @Nullable
  private static PhpSdkAdditionalData getSdkAdditionalData(@NotNull Project project) {
    try {
      final PhpCSConfiguration configuration = PhpCSProjectConfiguration.getInstance(project).findSelectedConfiguration(project, false);
      if (configuration == null || StringUtil.isEmpty(configuration.getInterpreterId())) {
        return null;
      }

      final PhpInterpreter id = PhpInterpretersManagerImpl.getInstance(project).findInterpreterById(configuration.getInterpreterId());
      return id != null ? id.getPhpSdkAdditionalData() : null;
    }
    catch (QualityToolValidationException e) {
      return null;
    }
  }

  private void createUIComponents() {
    myJBIntSpinner = new JBIntSpinner(4, 0, 8);
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

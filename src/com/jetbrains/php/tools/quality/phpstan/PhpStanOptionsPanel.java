package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.jetbrains.php.config.interpreters.PhpTextFieldWithSdkBasedBrowse;
import com.jetbrains.php.tools.quality.QualityToolsOptionsPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

public class PhpStanOptionsPanel extends QualityToolsOptionsPanel {
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

  private void createUIComponents() {
    myJBIntSpinner = new JBIntSpinner(4, 0, 8);
  }

  @Override
  public JPanel getOptionsPanel() {
    return myOptionsPanel;
  }

  public void init() {
  }
}

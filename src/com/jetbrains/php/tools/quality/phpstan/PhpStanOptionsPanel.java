package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.jetbrains.php.config.interpreters.PhpTextFieldWithSdkBasedBrowse;
import com.jetbrains.php.tools.quality.QualityToolConfigurationComboBox;
import com.jetbrains.php.tools.quality.QualityToolsOptionsPanel;

import javax.swing.*;

public class PhpStanOptionsPanel extends QualityToolsOptionsPanel {
  private JPanel myOptionsPanel;
  private JBCheckBox myFullProjectRunJBCheckBox;
  private JBTextField myMemoryLimitTextField;
  private JBIntSpinner myJBIntSpinner;
  private PhpTextFieldWithSdkBasedBrowse myConfigPathTextField;
  private PhpTextFieldWithSdkBasedBrowse myAutoloadPathTextField;
  private Project myProject;

  public PhpStanOptionsPanel(Project project, QualityToolConfigurationComboBox comboBox) {
    myProject = project;
    PhpStanProjectConfiguration configuration = PhpStanProjectConfiguration.getInstance(project);
    myFullProjectRunJBCheckBox.setSelected(configuration.isFullProject());
    myMemoryLimitTextField.setText(configuration.getMemoryLimit());
    myJBIntSpinner.setNumber(configuration.getLevel());
    myConfigPathTextField.setText(configuration.getConfig());
    myConfigPathTextField
      .init(project, getSdkAdditionalData(project, comboBox), PhpStanBundle.message("phpstan.configuration.file"), true, false);
    myAutoloadPathTextField.setText(configuration.getAutoload());
    myAutoloadPathTextField
      .init(project, getSdkAdditionalData(project, comboBox), PhpStanBundle.message("phpstan.autoload.file"), true, false);
  }

  private void createUIComponents() {
    myJBIntSpinner = new JBIntSpinner(4, 0, 8);
  }

  @Override
  public JPanel getOptionsPanel() {
    return myOptionsPanel;
  }

  @Override
  public void reset() {
    PhpStanProjectConfiguration configuration = PhpStanProjectConfiguration.getInstance(myProject);
    myFullProjectRunJBCheckBox.setSelected(configuration.isFullProject());
    myMemoryLimitTextField.setText(configuration.getMemoryLimit());
    myJBIntSpinner.setNumber(configuration.getLevel());
    myConfigPathTextField.setText(configuration.getConfig());
    myAutoloadPathTextField.setText(configuration.getAutoload());
  }

  @Override
  public boolean isModified() {
    PhpStanProjectConfiguration configuration = PhpStanProjectConfiguration.getInstance(myProject);
    if (myFullProjectRunJBCheckBox.isSelected() != configuration.isFullProject()) return true;
    if (!StringUtil.equals(myMemoryLimitTextField.getText(), configuration.getMemoryLimit())) return true;
    if (myJBIntSpinner.getNumber() != configuration.getLevel()) return true;
    if (!StringUtil.equals(myConfigPathTextField.getText(), configuration.getConfig())) return true;
    if (!StringUtil.equals(myAutoloadPathTextField.getText(), configuration.getAutoload())) return true;
    return false;
  }

  @Override
  public void apply() {
    PhpStanProjectConfiguration configuration = PhpStanProjectConfiguration.getInstance(myProject);
    configuration.setFullProject(myFullProjectRunJBCheckBox.isSelected());
    configuration.setMemoryLimit(myMemoryLimitTextField.getText());
    configuration.setLevel(myJBIntSpinner.getNumber());
    configuration.setConfig(myConfigPathTextField.getText());
    configuration.setAutoload(myAutoloadPathTextField.getText());
  }
}

package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.jetbrains.php.config.interpreters.PhpInterpreter;
import com.jetbrains.php.config.interpreters.PhpTextFieldWithSdkBasedBrowse;
import com.jetbrains.php.tools.quality.QualityToolConfigurationComboBox;
import com.jetbrains.php.tools.quality.QualityToolType;
import com.jetbrains.php.tools.quality.QualityToolsOptionsPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.net.URL;

import static com.intellij.openapi.vfs.VfsUtil.findFileByURL;
import static com.intellij.openapi.vfs.VfsUtilCore.convertToURL;
import static com.intellij.openapi.vfs.VfsUtilCore.pathToUrl;

public class PhpStanOptionsPanel extends QualityToolsOptionsPanel {
  private JPanel myOptionsPanel;
  private JBCheckBox myFullProjectRunJBCheckBox;
  private JBTextField myMemoryLimitTextField;
  private JBIntSpinner myJBIntSpinner;
  private PhpTextFieldWithSdkBasedBrowse myConfigPathTextField;
  private PhpTextFieldWithSdkBasedBrowse myAutoloadPathTextField;
  private final QualityToolConfigurationComboBox myComboBox;

  public PhpStanOptionsPanel(Project project,
                             QualityToolConfigurationComboBox comboBox,
                             Runnable validate) {
    super(project, validate, PhpStanQualityToolType.INSTANCE);
    myComboBox = comboBox;
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
    myConfigPathTextField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
      @Override
      protected void textChanged(@NotNull DocumentEvent e) {
        validate.run();
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

  @Override
  protected @Nullable String validatePath() {
    PhpInterpreter interpreter = getSelectedInterpreter(myProject, myComboBox);
    if (interpreter != null && interpreter.isRemote()) {
      //TODO: validate remote path?
      return null;
    }
    final URL url = convertToURL(pathToUrl(myConfigPathTextField.getText()));
    if (url == null || findFileByURL(url) == null) {
      return PhpStanBundle.message("config.file.doesnt.exist");
    }
    return null;
  }
}

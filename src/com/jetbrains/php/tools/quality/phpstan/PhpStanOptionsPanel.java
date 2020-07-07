package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.jetbrains.php.config.interpreters.PhpTextFieldWithSdkBasedBrowse;
import com.jetbrains.php.tools.quality.QualityToolCommonConfigurable;
import com.jetbrains.php.tools.quality.QualityToolsOptionsPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

import static com.jetbrains.php.lang.inspections.PhpInspectionsUtil.createPanelWithSettingsLink;
import static com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurationBaseManager.PHP_STAN;

public class PhpStanOptionsPanel extends QualityToolsOptionsPanel {
  private final PhpStanValidationInspection myInspection;
  private JPanel myOptionsPanel;
  private JBCheckBox myFullProjectRunJBCheckBox;
  private JBTextField myMemoryLimitTextField;
  private JBIntSpinner myJBIntSpinner;
  private PhpTextFieldWithSdkBasedBrowse myConfigPathTextField;
  private PhpTextFieldWithSdkBasedBrowse myAutoloadPathTextField;
  private JPanel myLinkPanel;

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
    DataManager.getInstance().getDataContextFromFocusAsync().onSuccess(context -> {
      Project project = CommonDataKeys.PROJECT.getData(context);
      if (project == null) {
        project = ProjectManager.getInstance().getDefaultProject();
      }
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
    });
    myAutoloadPathTextField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
      @Override
      protected void textChanged(@NotNull DocumentEvent e) {
        myInspection.autoload = myAutoloadPathTextField.getText();
      }
    });
  }

  private void createUIComponents() {
    myJBIntSpinner = new JBIntSpinner(4, 0, 8);
    myLinkPanel = createPanelWithSettingsLink(PHP_STAN,
                                              QualityToolCommonConfigurable.class,
                                              QualityToolCommonConfigurable::new,
                                              i -> i.showConfigurable(PHP_STAN));
  }

  @Override
  public JPanel getOptionsPanel() {
    return myOptionsPanel;
  }

  public void init() {
  }
}

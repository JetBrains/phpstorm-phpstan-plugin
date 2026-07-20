package com.jetbrains.php.tools.quality.phpstan

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.JBIntSpinner
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.jetbrains.php.config.interpreters.PhpTextFieldWithSdkBasedBrowse
import com.jetbrains.php.tools.quality.QualityToolConfigurationComboBox
import com.jetbrains.php.tools.quality.QualityToolsOptionsPanel
import com.jetbrains.php.tools.quality.ui.QualityToolRateLimitPanel
import com.jetbrains.php.tools.quality.ui.QualityToolRateLimitUI
import javax.swing.JPanel
import javax.swing.event.DocumentEvent

class PhpStanOptionsPanel(
  project: Project,
  private val myComboBox: QualityToolConfigurationComboBox<*>,
  validate: Runnable,
) : QualityToolsOptionsPanel(project, validate, PhpStanQualityToolType.INSTANCE) {

  private val myJBIntSpinner = JBIntSpinner(4, 0, 8)
  private val myMemoryLimitTextField = JBTextField()
  private val myConfigPathTextField = PhpTextFieldWithSdkBasedBrowse()
  private val myAutoloadPathTextField = PhpTextFieldWithSdkBasedBrowse()
  private val myRateLimitPanel = QualityToolRateLimitPanel()
  private lateinit var myFullProjectRunJBCheckBox: JBCheckBox
  private lateinit var myEditorModeJBCheckBox: JBCheckBox

  private val myOptionsPanel = panel {
    row {
      myFullProjectRunJBCheckBox = checkBox(PhpStanBundle.message("phpstan.checkbox.full.project.run")).component
    }
    row {
      myEditorModeJBCheckBox = checkBox(PhpStanBundle.message("phpstan.checkbox.editor.mode")).component
    }
    row(PhpStanBundle.message("label.level")) {
      cell(myJBIntSpinner)
    }
    row(PhpStanBundle.message("label.configuration.file")) {
      cell(myConfigPathTextField).align(AlignX.FILL)
    }
    row(PhpStanBundle.message("label.autoload.file")) {
      cell(myAutoloadPathTextField).align(AlignX.FILL)
    }
    row(PhpStanBundle.message("phpstan.label.options")) {
      cell(myMemoryLimitTextField).align(AlignX.FILL)
    }
    row {
      cell(myRateLimitPanel).align(AlignX.FILL)
    }
  }

  init {
    val configuration = PhpStanOptionsConfiguration.getInstance(project)
    myFullProjectRunJBCheckBox.isSelected = configuration.isFullProject
    myEditorModeJBCheckBox.isSelected = configuration.isEditorMode
    myMemoryLimitTextField.text = configuration.memoryLimit
    myJBIntSpinner.number = configuration.level
    myRateLimitPanel.configure(QualityToolRateLimitUI.DEFAULT_UI)
    myRateLimitPanel.reset(configuration.rateLimitSettings)
    myConfigPathTextField.text = configuration.config
    myConfigPathTextField.init(project, getSdkAdditionalData(project, myComboBox),
                               PhpStanBundle.message("phpstan.configuration.file"), true, false)
    myAutoloadPathTextField.text = configuration.autoload
    myAutoloadPathTextField.init(project, getSdkAdditionalData(project, myComboBox),
                                 PhpStanBundle.message("phpstan.autoload.file"), true, false)
    myConfigPathTextField.textField.document.addDocumentListener(object : DocumentAdapter() {
      override fun textChanged(e: DocumentEvent) {
        validate.run()
      }
    })
  }

  override fun getOptionsPanel(): JPanel = myOptionsPanel

  override fun reset() {
    val configuration = PhpStanOptionsConfiguration.getInstance(myProject)
    myFullProjectRunJBCheckBox.isSelected = configuration.isFullProject
    myEditorModeJBCheckBox.isSelected = configuration.isEditorMode
    myMemoryLimitTextField.text = configuration.memoryLimit
    myJBIntSpinner.number = configuration.level
    myRateLimitPanel.reset(configuration.rateLimitSettings)
    myConfigPathTextField.text = configuration.config
    myAutoloadPathTextField.text = configuration.autoload
  }

  override fun isModified(): Boolean {
    val configuration = PhpStanOptionsConfiguration.getInstance(myProject)
    if (myFullProjectRunJBCheckBox.isSelected != configuration.isFullProject) return true
    if (myEditorModeJBCheckBox.isSelected != configuration.isEditorMode) return true
    if (!StringUtil.equals(myMemoryLimitTextField.text, configuration.memoryLimit)) return true
    if (myJBIntSpinner.number != configuration.level) return true
    if (myRateLimitPanel.isModified(configuration.rateLimitSettings)) return true
    if (!StringUtil.equals(myConfigPathTextField.text, configuration.config)) return true
    if (!StringUtil.equals(myAutoloadPathTextField.text, configuration.autoload)) return true
    return false
  }

  override fun apply() {
    val configuration = PhpStanOptionsConfiguration.getInstance(myProject)
    configuration.isFullProject = myFullProjectRunJBCheckBox.isSelected
    configuration.isEditorMode = myEditorModeJBCheckBox.isSelected
    configuration.memoryLimit = myMemoryLimitTextField.text
    configuration.level = myJBIntSpinner.number
    myRateLimitPanel.applyTo(configuration.rateLimitSettings)
    configuration.config = myConfigPathTextField.text
    configuration.autoload = myAutoloadPathTextField.text
  }

  override fun validatePath(): String? {
    val interpreter = getSelectedInterpreter(myProject, myComboBox)
    if (interpreter != null && interpreter.isRemote) {
      //TODO: validate remote path?
      return null
    }
    val url = VfsUtilCore.convertToURL(VfsUtilCore.pathToUrl(myConfigPathTextField.text))
    if (url == null || VfsUtil.findFileByURL(url) == null) {
      return PhpStanBundle.message("config.file.doesnt.exist")
    }
    return null
  }
}

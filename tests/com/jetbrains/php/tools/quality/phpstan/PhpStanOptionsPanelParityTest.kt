package com.jetbrains.php.tools.quality.phpstan

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.ui.JBIntSpinner
import com.intellij.util.ui.UIUtil
import com.jetbrains.php.config.interpreters.PhpTextFieldWithSdkBasedBrowse
import com.jetbrains.php.tools.quality.QualityToolConfigurationComboBox
import com.jetbrains.php.tools.quality.ui.QualityToolRateLimitPanel
import javax.swing.JCheckBox
import javax.swing.JLabel

class PhpStanOptionsPanelParityTest : BasePlatformTestCase() {

  private fun comboBox() = QualityToolConfigurationComboBox(project, PhpStanQualityToolType.INSTANCE)

  private fun createPanel(): PhpStanOptionsPanel = PhpStanOptionsPanel(project, comboBox(), Runnable {})

  private fun config(): PhpStanOptionsConfiguration = PhpStanOptionsConfiguration.getInstance(project)

  fun testResetIsNotModifiedThenDetectsChange() {
    config().apply {
      setFullProject(true)
      setEditorMode(false)
      setMemoryLimit("512M")
      setLevel(5)
      setConfig("/tmp/phpstan.neon")
      setAutoload("/tmp/autoload.php")
    }
    val panel = createPanel()
    panel.reset()
    assertFalse("Freshly reset panel must not be modified", panel.isModified)

    config().setFullProject(false)
    assertTrue("isModified must detect the checkbox differing from the configuration", panel.isModified)
  }

  fun testApplyWritesUiStateBack() {
    config().apply {
      setFullProject(true)
      setEditorMode(true)
      setMemoryLimit("512M")
      setLevel(5)
      setConfig("/tmp/phpstan.neon")
      setAutoload("/tmp/autoload.php")
    }
    val panel = createPanel()
    panel.reset()

    // mutate the configuration underneath; apply() must push the (reset) UI state back over it
    config().apply {
      setFullProject(false)
      setEditorMode(false)
      setMemoryLimit("1G")
      setLevel(2)
      setConfig("/other.neon")
      setAutoload("/other.php")
    }
    panel.apply()
    assertTrue(config().isFullProject)
    assertTrue(config().isEditorMode)
    assertEquals("512M", config().memoryLimit)
    assertEquals(5, config().level)
    assertEquals("/tmp/phpstan.neon", config().config)
    assertEquals("/tmp/autoload.php", config().autoload)
  }

  fun testStructurePlacesControls() {
    val root = createPanel().getOptionsPanel()

    val checkboxTexts = UIUtil.findComponentsOfType(root, JCheckBox::class.java)
      .mapNotNull { it.text?.filterNot(Char::isISOControl) }
    assertTrue("Full-project-run checkbox missing: $checkboxTexts", checkboxTexts.any { it.contains("Full project run") })
    assertTrue("Editor-mode checkbox missing: $checkboxTexts", checkboxTexts.any { it.contains("Editor mode") })

    assertTrue("Both SDK-based path fields (config + autoload) must be placed",
               UIUtil.findComponentsOfType(root, PhpTextFieldWithSdkBasedBrowse::class.java).size >= 2)
    assertNotNull("Level spinner missing", UIUtil.findComponentOfType(root, JBIntSpinner::class.java))
    assertNotNull("Rate-limit sub-panel missing", UIUtil.findComponentOfType(root, QualityToolRateLimitPanel::class.java))

    val labels = UIUtil.findComponentsOfType(root, JLabel::class.java).mapNotNull { it.text?.filterNot(Char::isISOControl) }
    assertTrue("Configuration-file label missing: $labels", labels.any { it.contains("Configuration file") })
    assertTrue("Level label missing: $labels", labels.any { it.contains("Level") })
    assertTrue("Memory-limit label missing: $labels", labels.any { it.contains("Memory limit") })
  }
}

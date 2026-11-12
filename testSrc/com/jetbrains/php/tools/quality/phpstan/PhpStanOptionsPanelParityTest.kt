package com.jetbrains.php.tools.quality.phpstan

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightProjectFixture
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.TestDisposable
import com.intellij.ui.JBIntSpinner
import com.intellij.util.ui.UIUtil
import com.jetbrains.php.config.interpreters.PhpTextFieldWithSdkBasedBrowse
import com.jetbrains.php.tools.quality.QualityToolConfigurationComboBox
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.swing.JCheckBox
import javax.swing.JLabel

@TestApplication
class PhpStanOptionsPanelParityTest {
  private val projectFixture = codeInsightProjectFixture()
  private val project get() = projectFixture.get()

  @TestDisposable
  lateinit var testRootDisposable: Disposable

  // the combo box is a Disposable that holds the project; nothing else owns it in the test
  private fun comboBox() = QualityToolConfigurationComboBox(project, PhpStanQualityToolType.INSTANCE)
    .also { Disposer.register(testRootDisposable, it) }

  private fun createPanel(): PhpStanOptionsPanel = PhpStanOptionsPanel(project, comboBox(), Runnable {})

  private fun config(): PhpStanOptionsConfiguration = PhpStanOptionsConfiguration.getInstance(project)

  @Test
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
    assertFalse(panel.isModified, "Freshly reset panel must not be modified")

    config().setFullProject(false)
    assertTrue(panel.isModified, "isModified must detect the checkbox differing from the configuration")
  }

  @Test
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

  @Test
  fun testStructurePlacesControls() {
    val root = createPanel().getOptionsPanel()

    val checkboxTexts = UIUtil.findComponentsOfType(root, JCheckBox::class.java)
      .mapNotNull { it.text?.filterNot(Char::isISOControl) }
    assertTrue(checkboxTexts.any { it.contains("Full project run") }, "Full-project-run checkbox missing: $checkboxTexts")
    assertTrue(checkboxTexts.any { it.contains("Editor mode") }, "Editor-mode checkbox missing: $checkboxTexts")

    assertTrue(UIUtil.findComponentsOfType(root, PhpTextFieldWithSdkBasedBrowse::class.java).size >= 2,
               "Both SDK-based path fields (config + autoload) must be placed")
    assertNotNull(UIUtil.findComponentOfType(root, JBIntSpinner::class.java), "Level spinner missing")

    val labels = UIUtil.findComponentsOfType(root, JLabel::class.java).mapNotNull { it.text?.filterNot(Char::isISOControl) }
    assertTrue(labels.any { it.contains("Configuration file") }, "Configuration-file label missing: $labels")
    assertTrue(labels.any { it.contains("Level") }, "Level label missing: $labels")
    assertTrue(labels.any { it.contains("Memory limit") }, "Memory-limit label missing: $labels")
  }
}

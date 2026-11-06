package com.jetbrains.php.phpstan.quality.tools

import com.intellij.openapi.util.Version
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightProjectFixture
import com.intellij.testFramework.junit5.TestApplication
import com.jetbrains.php.tools.quality.phpstan.PhpStanAnnotatorProxy
import com.jetbrains.php.tools.quality.phpstan.PhpStanOptionsConfiguration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@TestApplication
class PhpStanEditorModeTest {
  private val projectFixture = codeInsightProjectFixture()
  private val project get() = projectFixture.get()

  @Test
  fun testEditorModeEnabledByDefault() {
    val configuration = PhpStanOptionsConfiguration.getInstance(project)
    assertTrue(configuration.isEditorMode, "Editor mode should be enabled by default")
  }

  @Test
  fun testEditorModePersistence() {
    val configuration = PhpStanOptionsConfiguration.getInstance(project)
    assertTrue(configuration.isEditorMode)
    configuration.isEditorMode = false
    assertFalse(configuration.isEditorMode)
    configuration.isEditorMode = true
    assertTrue(configuration.isEditorMode)
  }

  @Test
  fun testEditorModeStatePreservation() {
    val configuration = PhpStanOptionsConfiguration.getInstance(project)
    val state = configuration.state
    assertNotNull(state)
    assertTrue(state!!.isEditorMode, "State should preserve default editor mode value")
    configuration.isEditorMode = false
    val updatedState = configuration.state
    assertNotNull(updatedState)
    assertFalse(updatedState!!.isEditorMode, "State should preserve disabled editor mode")
  }

  @Test
  fun testParseVersionFromStandardOutput() {
    val version = PhpStanAnnotatorProxy.parseVersionFromOutput("PHPStan - PHP Static Analysis Tool 1.12.27")
    assertNotNull(version)
    assertEquals(1, version!!.major)
    assertEquals(12, version.minor)
    assertEquals(27, version.bugfix)
  }

  @Test
  fun testParseVersionFromVersion2Output() {
    val version = PhpStanAnnotatorProxy.parseVersionFromOutput("PHPStan - PHP Static Analysis Tool 2.1.17")
    assertNotNull(version)
    assertEquals(2, version!!.major)
    assertEquals(1, version.minor)
    assertEquals(17, version.bugfix)
  }

  @Test
  fun testParseVersionFromDevOutput() {
    val version = PhpStanAnnotatorProxy.parseVersionFromOutput("PHPStan - PHP Static Analysis Tool 0.12.25-dev@41b16d5")
    assertNotNull(version)
    assertEquals(0, version!!.major)
    assertEquals(12, version.minor)
    assertEquals(25, version.bugfix)
  }

  @Test
  fun testParseVersionFromEmptyOutput() {
    assertNull(PhpStanAnnotatorProxy.parseVersionFromOutput(""))
  }

  @Test
  fun testParseVersionFromOutputWithNoVersion() {
    assertNull(PhpStanAnnotatorProxy.parseVersionFromOutput("PHPStan - PHP Static Analysis Tool"))
  }

  @Test
  fun testSupportsEditorModeNull() {
    assertFalse(PhpStanAnnotatorProxy.supportsEditorMode(null))
  }

  @Test
  fun testSupportsEditorMode1xOld() {
    assertFalse(PhpStanAnnotatorProxy.supportsEditorMode(Version(1, 12, 26)))
  }

  @Test
  fun testSupportsEditorMode1xExact() {
    assertTrue(PhpStanAnnotatorProxy.supportsEditorMode(Version(1, 12, 27)))
  }

  @Test
  fun testSupportsEditorMode1xNewer() {
    assertTrue(PhpStanAnnotatorProxy.supportsEditorMode(Version(1, 12, 28)))
  }

  @Test
  fun testSupportsEditorMode2xOld() {
    assertFalse(PhpStanAnnotatorProxy.supportsEditorMode(Version(2, 1, 16)))
  }

  @Test
  fun testSupportsEditorMode2xExact() {
    assertTrue(PhpStanAnnotatorProxy.supportsEditorMode(Version(2, 1, 17)))
  }

  @Test
  fun testSupportsEditorMode2xNewer() {
    assertTrue(PhpStanAnnotatorProxy.supportsEditorMode(Version(2, 2, 0)))
  }
}

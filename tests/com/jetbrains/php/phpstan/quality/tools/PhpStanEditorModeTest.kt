package com.jetbrains.php.phpstan.quality.tools

import com.intellij.openapi.util.Version
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase
import com.jetbrains.php.tools.quality.phpstan.PhpStanAnnotatorProxy
import com.jetbrains.php.tools.quality.phpstan.PhpStanOptionsConfiguration
import junit.framework.TestCase

class PhpStanEditorModeTest : CodeInsightFixtureTestCase<Nothing?>() {

  fun testEditorModeEnabledByDefault() {
    val configuration = PhpStanOptionsConfiguration.getInstance(project)
    assertTrue("Editor mode should be enabled by default", configuration.isEditorMode)
  }

  fun testEditorModePersistence() {
    val configuration = PhpStanOptionsConfiguration.getInstance(project)
    assertTrue(configuration.isEditorMode)
    configuration.isEditorMode = false
    assertFalse(configuration.isEditorMode)
    configuration.isEditorMode = true
    assertTrue(configuration.isEditorMode)
  }

  fun testEditorModeStatePreservation() {
    val configuration = PhpStanOptionsConfiguration.getInstance(project)
    val state = configuration.state
    assertNotNull(state)
    assertTrue("State should preserve default editor mode value", state!!.isEditorMode)
    configuration.isEditorMode = false
    val updatedState = configuration.state
    assertNotNull(updatedState)
    assertFalse("State should preserve disabled editor mode", updatedState!!.isEditorMode)
  }

  fun testParseVersionFromStandardOutput() {
    val version = PhpStanAnnotatorProxy.parseVersionFromOutput("PHPStan - PHP Static Analysis Tool 1.12.27")
    assertNotNull(version)
    TestCase.assertEquals(1, version!!.major)
    TestCase.assertEquals(12, version.minor)
    TestCase.assertEquals(27, version.bugfix)
  }

  fun testParseVersionFromVersion2Output() {
    val version = PhpStanAnnotatorProxy.parseVersionFromOutput("PHPStan - PHP Static Analysis Tool 2.1.17")
    assertNotNull(version)
    TestCase.assertEquals(2, version!!.major)
    TestCase.assertEquals(1, version.minor)
    TestCase.assertEquals(17, version.bugfix)
  }

  fun testParseVersionFromDevOutput() {
    val version = PhpStanAnnotatorProxy.parseVersionFromOutput("PHPStan - PHP Static Analysis Tool 0.12.25-dev@41b16d5")
    assertNotNull(version)
    TestCase.assertEquals(0, version!!.major)
    TestCase.assertEquals(12, version.minor)
    TestCase.assertEquals(25, version.bugfix)
  }

  fun testParseVersionFromEmptyOutput() {
    assertNull(PhpStanAnnotatorProxy.parseVersionFromOutput(""))
  }

  fun testParseVersionFromOutputWithNoVersion() {
    assertNull(PhpStanAnnotatorProxy.parseVersionFromOutput("PHPStan - PHP Static Analysis Tool"))
  }

  fun testSupportsEditorModeNull() {
    assertFalse(PhpStanAnnotatorProxy.supportsEditorMode(null))
  }

  fun testSupportsEditorMode1xOld() {
    assertFalse(PhpStanAnnotatorProxy.supportsEditorMode(Version(1, 12, 26)))
  }

  fun testSupportsEditorMode1xExact() {
    assertTrue(PhpStanAnnotatorProxy.supportsEditorMode(Version(1, 12, 27)))
  }

  fun testSupportsEditorMode1xNewer() {
    assertTrue(PhpStanAnnotatorProxy.supportsEditorMode(Version(1, 12, 28)))
  }

  fun testSupportsEditorMode2xOld() {
    assertFalse(PhpStanAnnotatorProxy.supportsEditorMode(Version(2, 1, 16)))
  }

  fun testSupportsEditorMode2xExact() {
    assertTrue(PhpStanAnnotatorProxy.supportsEditorMode(Version(2, 1, 17)))
  }

  fun testSupportsEditorMode2xNewer() {
    assertTrue(PhpStanAnnotatorProxy.supportsEditorMode(Version(2, 2, 0)))
  }
}

package com.jetbrains.php.phpstan.quality.tools

import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightProjectFixture
import com.intellij.testFramework.junit5.TestApplication
import com.jetbrains.php.tools.quality.phpstan.PhpStanGlobalInspection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

@TestApplication
class PhpStanCommandLineTest {
  private val projectFixture = codeInsightProjectFixture()
  private val project get() = projectFixture.get()

  @Test
  fun testEditorModeAppendsOpenFileAsAnalyzeTarget() {
    val openFile = "/src/Foo.php"
    val options = PhpStanGlobalInspection().getBaseCommandLineOptions(listOf("/tmp/buf.php"), project, openFile)
    assertEquals(openFile, options.last(), "Open file must be appended after --instead-of as the analyze target")
  }

  @Test
  fun testBaseOptionsCarryNoOutputFlags() {
    val options = PhpStanGlobalInspection().getBaseCommandLineOptions(listOf("/tmp/buf.php"), project)
    assertEquals("analyze", options.first())
    for (flag in listOf("--error-format=checkstyle", "--no-progress", "--no-ansi", "--no-interaction")) {
      assertFalse(options.contains(flag), flag)
    }
  }
}

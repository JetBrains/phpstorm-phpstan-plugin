package com.jetbrains.php.phpstan.quality.tools

import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase
import com.jetbrains.php.tools.quality.phpstan.PhpStanGlobalInspection

class PhpStanCommandLineTest : CodeInsightFixtureTestCase<Nothing?>() {

  fun testEditorModeAppendsOpenFileAsAnalyzeTarget() {
    val openFile = "/src/Foo.php"
    val options = PhpStanGlobalInspection().getBaseCommandLineOptions(listOf("/tmp/buf.php"), project, openFile)
    assertEquals("Open file must be appended after --instead-of as the analyze target", openFile, options.last())
  }

  fun testBaseOptionsCarryNoOutputFlags() {
    val options = PhpStanGlobalInspection().getBaseCommandLineOptions(listOf("/tmp/buf.php"), project)
    assertEquals("analyze", options.first())
    for (flag in listOf("--error-format=checkstyle", "--no-progress", "--no-ansi", "--no-interaction")) {
      assertFalse(flag, options.contains(flag))
    }
  }
}

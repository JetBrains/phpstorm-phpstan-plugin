package com.jetbrains.php.phpstan.quality.tools

import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase
import com.jetbrains.php.tools.quality.phpstan.PhpStanGlobalInspection

class PhpStanCommandLineTest : CodeInsightFixtureTestCase<Nothing?>() {

  fun testEditorModeAppendsOpenFileAsAnalyzeTarget() {
    val openFile = "/src/Foo.php"
    val options = PhpStanGlobalInspection().getCommandLineOptions(listOf("/tmp/buf.php"), project, openFile)
    assertEquals("Open file must be appended after --instead-of as the analyze target", openFile, options.last())
  }
}

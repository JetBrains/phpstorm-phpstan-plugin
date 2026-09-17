package com.jetbrains.php.tools.quality.phpstan

import com.intellij.profile.codeInspection.InspectionProfileManager
import com.intellij.testFramework.builders.ModuleFixtureBuilder
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase

class PhpStanAnnotatorOptionsTest : CodeInsightFixtureTestCase<ModuleFixtureBuilder<*>>() {
  override fun setUp() {
    super.setUp()
    PhpStanConfigurationManager.getInstance(myFixture.project).getOrCreateLocalSettings().toolPath = "phpstan"
    myFixture.configureByText("file.php", "<?php")
    myFixture.enableInspections(PhpStanGlobalInspection())
  }

  fun testOutputFlagsFollowTheAnalyzeCommand() {
    val annotator = PhpStanAnnotatorProxy.INSTANCE
    val profileName = InspectionProfileManager.getInstance(project).currentProfile.name
    val info = annotator.collectAnnotatorInfo(myFixture.file, myFixture.editor, project, profileName, false) as PhpStanQualityToolAnnotatorInfo
    val options = annotator.getOptions("/tmp/file.php", info)!!
    assertEquals("analyze", options.first())
    assertEquals(listOf("--error-format=checkstyle", "--no-progress", "--no-ansi", "--no-interaction"), options.subList(1, 5))
    assertTrue(options.any { it.startsWith("--memory-limit=") })
  }
}

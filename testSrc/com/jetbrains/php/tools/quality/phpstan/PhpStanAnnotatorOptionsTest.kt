package com.jetbrains.php.tools.quality.phpstan

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightFixture
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightProjectFixture
import com.intellij.profile.codeInspection.InspectionProfileManager
import com.intellij.testFramework.junit5.TestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class PhpStanAnnotatorOptionsTest {
  private val projectFixture = codeInsightProjectFixture()
  private val myFixture by codeInsightFixture(projectFixture)
  private val project get() = myFixture.project

  @BeforeEach
  fun setUp() {
    PhpStanConfigurationManager.getInstance(myFixture.project).getOrCreateLocalSettings().toolPath = "phpstan"
    myFixture.configureByText("file.php", "<?php")
    myFixture.enableInspections(PhpStanGlobalInspection())
  }

  @Test
  fun testOutputFlagsFollowTheAnalyzeCommand() {
    val annotator = PhpStanAnnotatorProxy.INSTANCE
    val profileName = InspectionProfileManager.getInstance(project).currentProfile.name
    val info = runReadActionBlocking { annotator.collectAnnotatorInfo(myFixture.file, myFixture.editor, project, profileName, false) }
    val options = annotator.getOptions("/tmp/file.php", info)!!
    assertEquals("analyze", options.first())
    assertEquals(listOf("--error-format=checkstyle", "--no-progress", "--no-ansi", "--no-interaction"), options.subList(1, 5))
    assertTrue(options.any { it.startsWith("--memory-limit=") })
  }
}

package com.jetbrains.php.phpstan.quality.tools

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightFixture
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightProjectFixture
import com.intellij.profile.codeInspection.InspectionProfileManager
import com.intellij.psi.PsiFile
import com.intellij.testFramework.junit5.TestApplication
import com.jetbrains.php.tools.quality.phpstan.PhpStanAnnotatorProxy
import com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurationManager
import com.jetbrains.php.tools.quality.phpstan.PhpStanGlobalInspection
import com.jetbrains.php.tools.quality.phpstan.PhpStanMessageProcessor
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class PhpStanAnnotatorTest {
  private val projectFixture = codeInsightProjectFixture()
  private val myFixture by codeInsightFixture(projectFixture)
  private val project get() = myFixture.project

  @BeforeEach
  fun setUp() {
    PhpStanConfigurationManager.getInstance(myFixture.project).getOrCreateLocalSettings().toolPath = "phpstan"
    myFixture.addFileToProject("file.txt", "")
    myFixture.configureByText("file.php", "<?php")
  }

  @Test
  fun testFile() {
    val messageProcessor = runPhpStanAnnotator(myFixture.file)
    assertNotNull(messageProcessor)
  }

  @Test
  fun testBatchMode() {
    val messageProcessor = runPhpStanAnnotator(null)
    assertNull(messageProcessor)
  }

  private fun runPhpStanAnnotator(file: PsiFile?): PhpStanMessageProcessor? {
    myFixture.enableInspections(PhpStanGlobalInspection())
    val annotator = PhpStanAnnotatorProxy.INSTANCE
    val info = runReadActionBlocking {
      annotator.collectAnnotatorInfo(file, myFixture.editor, project, InspectionProfileManager.getInstance(project).currentProfile.name, false)
    }
    if (info == null) {
      return null
    }

    return annotator.doAnnotate(info) as PhpStanMessageProcessor?
  }
}

package com.jetbrains.php.phpstan.quality.tools

import com.intellij.profile.codeInspection.InspectionProfileManager
import com.intellij.psi.PsiFile
import com.intellij.testFramework.builders.ModuleFixtureBuilder
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase
import com.jetbrains.php.tools.quality.phpstan.*

class PhpStanAnnotatorTest : CodeInsightFixtureTestCase<ModuleFixtureBuilder<*>>() {
  override fun setUp() {
    super.setUp()
    PhpStanConfigurationManager.getInstance(myFixture.project).getOrCreateLocalSettings().toolPath = "phpstan"
    myFixture.addFileToProject("file.txt", "")
    myFixture.configureByText("file.php", "<?php")
  }

  fun testFile() {
    val messageProcessor = runPhpStanAnnotator(myFixture.file)
    assertNotNull(messageProcessor)
  }

  fun testBatchMode() {
    val messageProcessor = runPhpStanAnnotator(null)
    assertNull(messageProcessor)
  }

  private fun runPhpStanAnnotator(file: PsiFile?): PhpStanMessageProcessor? {
    myFixture.enableInspections(PhpStanGlobalInspection())
    val annotator = PhpStanAnnotatorProxy.INSTANCE
    val info = annotator.collectAnnotatorInfo(file, myFixture.editor, project, InspectionProfileManager.getInstance(project).currentProfile.name, false) as PhpStanQualityToolAnnotatorInfo?
    if (info == null) {
      return null
    }

    return annotator.doAnnotate(info) as PhpStanMessageProcessor?
  }
}
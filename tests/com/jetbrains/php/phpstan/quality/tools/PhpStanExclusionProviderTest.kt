package com.jetbrains.php.phpstan.quality.tools

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.jetbrains.php.tools.quality.phpstan.exclusion.PhpStanExclusionProvider
import java.nio.file.Paths

class PhpStanExclusionProviderTest : BasePlatformTestCase() {
  private lateinit var provider: PhpStanExclusionProvider

  override fun setUp() {
    super.setUp()
    provider = PhpStanExclusionProvider()
  }

  fun testNoConfigFile() {
    val exclusions = provider.getExclusionDirectories(project)
    assertTrue(exclusions.isEmpty())
  }

  fun testConfigWithTmpDir() {
    val configContent = """
      parameters:
        tmpDir: .phpstan-cache
    """.trimIndent()

    WriteCommandAction.runWriteCommandAction(project) {
      myFixture.tempDirFixture.findOrCreateDir("")
        .createChildData(this, "phpstan.neon")
        .setBinaryContent(configContent.toByteArray())
    }

    val exclusions = provider.getExclusionDirectories(project)
    assertEquals(1, exclusions.size)
    assertEquals(".phpstan-cache", exclusions[0])
  }

  fun testConfigWithEnvironmentVariable() {
    val configContent = """
      parameters:
        tmpDir: %TMP%/phpstan
    """.trimIndent()

    WriteCommandAction.runWriteCommandAction(project) {
      myFixture.tempDirFixture.findOrCreateDir("")
        .createChildData(this, "phpstan.neon")
        .setBinaryContent(configContent.toByteArray())
    }

    val exclusions = provider.getExclusionDirectories(project)
    assertTrue(exclusions.isEmpty())
  }

  fun testInvalidYamlConfig() {
    val configContent = "invalid yaml {{{ content"

    WriteCommandAction.runWriteCommandAction(project) {
      myFixture.tempDirFixture.findOrCreateDir("")
        .createChildData(this, "phpstan.neon")
        .setBinaryContent(configContent.toByteArray())
    }

    val exclusions = provider.getExclusionDirectories(project)
    assertTrue(exclusions.isEmpty())
  }

  fun testConfigWithoutTmpDir() {
    val configContent = """
      parameters:
        level: 8
        paths:
          - src
    """.trimIndent()

    WriteCommandAction.runWriteCommandAction(project) {
      myFixture.tempDirFixture.findOrCreateDir("")
        .createChildData(this, "phpstan.neon")
        .setBinaryContent(configContent.toByteArray())
    }

    val exclusions = provider.getExclusionDirectories(project)
    assertTrue(exclusions.isEmpty())
  }

  fun testMultipleConfigFiles() {
    val configContent = """
      parameters:
        tmpDir: cache/phpstan
    """.trimIndent()

    WriteCommandAction.runWriteCommandAction(project) {
      val root = myFixture.tempDirFixture.findOrCreateDir("")
      root.createChildData(this, "phpstan.neon.dist")
        .setBinaryContent("parameters:\n  tmpDir: dist-cache".toByteArray())
      root.createChildData(this, "phpstan.neon")
        .setBinaryContent(configContent.toByteArray())
    }

    val exclusions = provider.getExclusionDirectories(project)
    assertEquals(1, exclusions.size)
    assertEquals("cache/phpstan", exclusions[0])
  }
}
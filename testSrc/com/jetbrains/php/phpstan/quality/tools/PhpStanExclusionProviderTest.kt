package com.jetbrains.php.phpstan.quality.tools

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightFixture
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightProjectFixture
import com.intellij.testFramework.junit5.TestApplication
import com.jetbrains.php.tools.quality.phpstan.exclusion.PhpStanExclusionProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class PhpStanExclusionProviderTest {
  private val projectFixture = codeInsightProjectFixture()
  private val myFixture by codeInsightFixture(projectFixture)
  private val project get() = myFixture.project
  private lateinit var provider: PhpStanExclusionProvider

  @BeforeEach
  fun setUp() {
    provider = PhpStanExclusionProvider()
  }

  @Test
  fun testNoConfigFile() {
    val exclusions = provider.getExclusionDirectories(project)
    assertTrue(exclusions.isEmpty())
  }

  @Test
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

  @Test
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

  @Test
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

  @Test
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

  @Test
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

  @Test
  fun testConfigWithTmpDirPointingToProjectRoot() {
    // PHPStan writes its cache into the project root with this configuration; there is no directory to exclude.
    for (tmpDir in listOf(".", "./", "\"\"", "'.'")) {
      createConfig("parameters:\n  tmpDir: $tmpDir")

      val exclusions = provider.getExclusionDirectories(project)
      assertTrue(exclusions.isEmpty(), "tmpDir '$tmpDir' must not produce an exclusion, got $exclusions")
    }
  }

  @Test
  fun testConfigWithAbsoluteTmpDir() {
    createConfig("parameters:\n  tmpDir: /tmp/phpstan")

    val exclusions = provider.getExclusionDirectories(project)
    assertTrue(exclusions.isEmpty())
  }

  @Test
  fun testConfigWithTmpDirOutsideProject() {
    createConfig("parameters:\n  tmpDir: ../shared-cache")

    val exclusions = provider.getExclusionDirectories(project)
    assertTrue(exclusions.isEmpty())
  }

  @Test
  fun testRelativeTmpDirIsNormalized() {
    createConfig("parameters:\n  tmpDir: ./cache/phpstan/")

    val exclusions = provider.getExclusionDirectories(project)
    assertThat(exclusions).containsExactly("cache/phpstan")
  }

  private fun createConfig(content: String) {
    WriteCommandAction.runWriteCommandAction(project) {
      val root = myFixture.tempDirFixture.findOrCreateDir("")
      val existing = root.findChild("phpstan.neon")
      val configFile = existing ?: root.createChildData(this, "phpstan.neon")
      configFile.setBinaryContent(content.toByteArray())
    }
  }
}
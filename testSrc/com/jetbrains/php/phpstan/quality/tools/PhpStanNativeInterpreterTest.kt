// Copyright 2000-2026 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.jetbrains.php.phpstan.quality.tools

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightProjectFixture
import com.intellij.testFramework.IndexingTestUtil
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.TestDisposable
import com.jetbrains.php.tools.quality.NativeQualityToolInterpreter
import com.jetbrains.php.tools.quality.phpstan.PhpStanConfiguration
import com.jetbrains.php.tools.quality.phpstan.PhpStanGlobalInspection
import com.jetbrains.php.tools.quality.phpstan.PhpStanOptionsConfiguration
import com.jetbrains.php.tools.quality.phpstan.PhpStanProjectConfiguration
import com.jetbrains.php.tools.quality.phpstan.PhpStanQualityToolType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class PhpStanNativeInterpreterTest {
  companion object {
    private val projectFixture = codeInsightProjectFixture()
  }

  private val project get() = projectFixture.get()

  @TestDisposable
  lateinit var testRootDisposable: Disposable

  @BeforeEach
  fun waitForIndexes() {
    IndexingTestUtil.waitUntilIndexesAreReady(project)
  }

  @Test
  fun testConfigAutoloadAndFilesArePathsInTheEnvironment() {
    val native = NativeQualityToolInterpreter.select(project, PhpStanQualityToolType.INSTANCE, PhpStanProjectConfiguration.getInstance(project), testRootDisposable) { id ->
      object : PhpStanConfiguration() {
        override fun getId(): String = id
        override fun getInterpreterId(): String = id
      }
    }
    val options = PhpStanOptionsConfiguration.getInstance(project)
    val savedConfig = options.config
    val savedAutoload = options.autoload
    Disposer.register(testRootDisposable) {
      options.config = savedConfig
      options.autoload = savedAutoload
    }
    options.config = native.hostPath("/home/user/project/phpstan.neon")
    options.autoload = native.hostPath("/home/user/project/vendor/autoload.php")

    val commandLine = PhpStanGlobalInspection().getBaseCommandLineOptions(listOf(native.hostPath("/home/user/project/src/Foo.php")), project)

    assertEquals("/home/user/project/phpstan.neon", commandLine[commandLine.indexOf("-c") + 1])
    assertEquals("/home/user/project/vendor/autoload.php", commandLine[commandLine.indexOf("-a") + 1])
    assertEquals("/home/user/project/src/Foo.php", commandLine.last())
  }
}

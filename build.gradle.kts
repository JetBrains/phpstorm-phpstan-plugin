import org.jetbrains.intellij.platform.gradle.tasks.VerifyPluginTask

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm")
  id("org.jetbrains.intellij.platform")
}

group = prop("pluginGroup")
version = prop("pluginVersion")

sourceSets {
  main {
    java.srcDir("src")
    resources.srcDir("resources")
    resources.srcDir("remoteInterpreter/resources")
  }
  test {
    java.srcDir("tests")
    java.include(
      "com/jetbrains/php/phpstan/quality/tools/PhpStanConfigFileFromComposerTest.java",
      "com/jetbrains/php/phpstan/quality/tools/PhpStanVersionValidatorTest.java",
    )
    resources.srcDir("testData")
  }
}

kotlin {
  sourceSets {
    main {
      kotlin.srcDir("src")
    }
    test {
      kotlin.srcDir("tests")
      kotlin.include(
        "com/jetbrains/php/phpstan/quality/tools/PhpStanAnnotatorTest.kt",
        "com/jetbrains/php/phpstan/quality/tools/PhpStanCommandLineTest.kt",
        "com/jetbrains/php/phpstan/quality/tools/PhpStanEditorModeTest.kt",
        "com/jetbrains/php/phpstan/quality/tools/PhpStanExclusionProviderTest.kt",
        "com/jetbrains/php/tools/quality/phpstan/PhpStanOptionsPanelParityTest.kt",
      )
    }
  }
}

dependencies {
  implementation(kotlin("stdlib"))
  testImplementation("junit:junit:4.13.2")
  testRuntimeOnly(project(":remoteInterpreter"))

  intellijPlatform {
    phpStanPlatform(project)
    pluginModule(runtimeOnly(project(":remoteInterpreter")))
  }
}

configureJvm()

tasks {
  test {
    systemProperty("idea.home.path", projectDir.absolutePath)
  }

  wrapper {
    gradleVersion = prop("gradleVersion")
  }

  patchPluginXml {
    version = prop("pluginVersion")
    sinceBuild.set(prop("pluginSinceBuild"))
    prop("pluginUntilBuild").takeIf(String::isNotEmpty)?.let(untilBuild::set)
  }

  verifyPlugin {
    failureLevel.set(
      failureLevel.get().filterNot { it == VerifyPluginTask.FailureLevel.INTERNAL_API_USAGES },
    )
  }
}

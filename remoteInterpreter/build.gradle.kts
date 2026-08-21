plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm")
  id("org.jetbrains.intellij.platform.module")
}

sourceSets {
  main {
    java.srcDir("src")
    resources.srcDir("resources")
  }
}

kotlin {
  sourceSets {
    main {
      kotlin.srcDir("src")
    }
  }
}

dependencies {
  implementation(kotlin("stdlib"))
  compileOnly(project(":"))

  intellijPlatform {
    phpStanPlatform(project)
    localPlugin(project(":"))
  }
}

configureJvm()

tasks.processResources {
  filesMatching("intellij.phpstan.remoteInterpreter.xml") {
    filter {
      when {
        it == "<idea-plugin>" -> "<idea-plugin package=\"com.intellij.phpstan.remoteInterpreter\">"
        it.contains("<module name=\"intellij.php.backend\"/>") -> ""
        else -> it
      }
    }
  }
}

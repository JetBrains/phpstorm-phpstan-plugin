import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.intellij.platform.gradle.ProductMode
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.extensions.IntelliJPlatformDependenciesExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

fun Project.prop(key: String): String {
  return providers.gradleProperty(key).getOrElse("")
}

fun IntelliJPlatformDependenciesExtension.phpStanPlatform(project: Project) {
  create(project.providers.gradleProperty("platformType"), project.providers.gradleProperty("platformVersion")) {
    useInstaller.set(false)
    productMode.set(ProductMode.MONOLITH)
  }
  testFramework(TestFrameworkType.Platform)

  bundledPlugin("com.jetbrains.php")
  bundledPlugin("JavaScript")
  bundledPlugin("com.intellij.modules.jna")
  bundledPlugin("com.intellij.modules.pty4j")
  bundledPlugin("com.intellij.problemsView")
  bundledPlugin("com.intellij.structureView")
  bundledPlugin("com.intellij.platform.testRunner")
  bundledPlugin("com.intellij.structuralSearch")
  bundledPlugin("org.jetbrains.plugins.phpstorm-remote-interpreter")
  bundledModule("intellij.platform.webide.impl")
}

fun Project.configureJvm() {
  val javaVersion = prop("javaVersion")
  configure<KotlinJvmProjectExtension> {
    jvmToolchain {
      languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
    compilerOptions {
      jvmTarget.set(JvmTarget.fromTarget(javaVersion))
    }
  }
  tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
  }
}

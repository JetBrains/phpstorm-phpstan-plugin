package integration.tests.phpstan

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.util.childrenOfType
import com.intellij.testFramework.junit5.TestApplication
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.fixtures.PhpCodeInsightTestFixture.Companion.phpCodeInsightFixture
import com.jetbrains.php.fixtures.PhpTestDataLayout
import com.jetbrains.php.fixtures.phpProjectFixture
import com.jetbrains.php.lang.psi.elements.impl.ArrayCreationExpressionImpl
import com.jetbrains.php.lang.psi.elements.impl.ForeachImpl
import com.jetbrains.php.phpstan.lang.documentation.parser.PhpStanDocParserTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.zip.ZipInputStream
import kotlin.io.path.div

@TestApplication
class PhpStanNewTagsTest {
  companion object {
    private val projectFixture = phpProjectFixture()
  }

  private val php by phpCodeInsightFixture(projectFixture, PhpTestDataLayout("newTags").withHome(PhpStanDocParserTest.TEST_DATA_HOME))

  private lateinit var phpstanFolder: File

  @BeforeEach
  fun setUp() {
    phpstanFolder = File(PathManager.getHomePath() + "/" + php.basePath)
    FileUtil.createDirectory(phpstanFolder)
    val zipUrl = URL("https://github.com/phpstan/phpstan-src/archive/refs/heads/1.10.x.zip")
    downloadAndUnpackZip(zipUrl, phpstanFolder)
  }

  @Test
  fun testNewTags() {
    php.fixture.copyFileToProject("phpstan-src-1.10.x/src/PhpDoc/PhpDocNodeResolver.php")
    val annotations = mutableListOf<String>()
    runReadActionBlocking {
      val classWithAnnotations = PhpIndex.getInstance(php.project).getClassesByFQN("PHPStan\\PhpDoc\\PhpDocNodeResolver").first()
      classWithAnnotations.methods.forEach { method ->
        method.lastChild.childrenOfType<ForeachImpl>().forEach{ forEach ->
          if(forEach.array is ArrayCreationExpressionImpl){
            (forEach.array as ArrayCreationExpressionImpl).values().forEach {
              annotations.add(it.text)
            }
          }
        }
      }
    }
    val resultFileName = "phpstan-tags.txt"
    val resultFile = (phpstanFolder.toPath() / resultFileName).toFile()
    resultFile.writeText(annotations.sorted().joinToString("\n"))
    println("##teamcity[publishArtifacts '${resultFile.absolutePath}']")
    val pathToPreviousResults = (phpstanFolder.toPath() / "previousResults" / resultFileName).toFile()
    if(pathToPreviousResults.exists()){
      assertThat(annotations).containsExactlyInAnyOrderElementsOf(pathToPreviousResults.readLines())
    } else {
      println("Previous results are not found at ${pathToPreviousResults.absolutePath}")
    }
  }
  private fun downloadAndUnpackZip(zipUrl: URL, destinationDirectory: File) {
    val zipConnection = zipUrl.openConnection()
    val zipInputStream = ZipInputStream(BufferedInputStream(zipConnection.getInputStream()))
    var zipEntry = zipInputStream.nextEntry
    while (zipEntry != null) {
      val file = File(destinationDirectory, zipEntry.name)
      if (zipEntry.isDirectory) {
        file.mkdirs()
      }
      else {
        val fileOutputStream = FileOutputStream(file)
        val bufferedOutputStream = BufferedOutputStream(fileOutputStream, 2048)
        val data = ByteArray(2048)
        var count: Int
        while (zipInputStream.read(data, 0, data.size).also { count = it } != -1) {
          bufferedOutputStream.write(data, 0, count)
        }
        bufferedOutputStream.flush()
        bufferedOutputStream.close()
      }
      zipInputStream.closeEntry()
      zipEntry = zipInputStream.nextEntry
    }
    zipInputStream.close()
  }
}


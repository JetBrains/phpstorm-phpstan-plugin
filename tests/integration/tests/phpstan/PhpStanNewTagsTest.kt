package integration.tests.phpstan

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.util.childrenOfType
import com.intellij.testFramework.UsefulTestCase
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.fixtures.PhpCodeInsightFixtureTestCase
import com.jetbrains.php.lang.psi.elements.impl.ArrayCreationExpressionImpl
import com.jetbrains.php.lang.psi.elements.impl.ForeachImpl
import com.jetbrains.php.phpstan.lang.documentation.parser.PhpStanDocParserTest
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.zip.ZipInputStream
import kotlin.io.path.div

class PhpStanNewTagsTest : PhpCodeInsightFixtureTestCase() {

  private lateinit var phpstanFolder: File
  override fun getFixtureTestDataFolder(): String {
    return "newTags"
  }

  override fun getTestDataHome(): String {
    return PhpStanDocParserTest.TEST_DATA_HOME
  }


  override fun setUp() {
    super.setUp()
    phpstanFolder = File(PathManager.getHomePath() + "/" + basePath)
    FileUtil.createDirectory(phpstanFolder)
    val zipUrl = URL("https://github.com/phpstan/phpstan-src/archive/refs/heads/1.10.x.zip")
    downloadAndUnpackZip(zipUrl, phpstanFolder)
  }

  override fun tearDown() {
    try {
      //FileUtil.deleteRecursively(phpstanFolder.toPath())
    }
    catch (e: Throwable) {
      addSuppressedException(e)
    }
    finally {
      super.tearDown()
    }
  }

  fun testNewTags() {
    myFixture.copyFileToProject("phpstan-src-1.10.x/src/PhpDoc/PhpDocNodeResolver.php")
    val classWithAnnotations = PhpIndex.getInstance(project).getClassesByFQN("PHPStan\\PhpDoc\\PhpDocNodeResolver").first()
    val annotations = mutableListOf<String>()
    classWithAnnotations.methods.forEach { method ->
      method.lastChild.childrenOfType<ForeachImpl>().forEach{ forEach ->
        if(forEach.array is ArrayCreationExpressionImpl){
          (forEach.array as ArrayCreationExpressionImpl).values().forEach {
            annotations.add(it.text)
          }
        }
      }
    }
    val resultFileName = "phpstan-tags.txt"
    val resultFile = FileUtil.createTempFile(resultFileName, "")
    resultFile.writeText(annotations.joinToString("\n"))
    println("##teamcity[publishArtifacts '${resultFile.absolutePath}']")
    val pathToPreviousResults = (phpstanFolder.toPath() / "previousResults" / resultFileName).toFile()
    if(pathToPreviousResults.exists()){
      UsefulTestCase.assertSameElements(annotations, pathToPreviousResults.readLines())
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


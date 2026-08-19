package com.jetbrains.php.tools.quality.phpstan.exclusion

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.io.OSAgnosticPathUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.PathUtilRt
import com.intellij.webcore.resourceRoots.FrameworkExclusionProvider
import org.yaml.snakeyaml.Yaml

/**
 * Exclusion provider for PHPStan cache directories.
 * 
 * PHPStan stores its cache in:
 * - Default: sys_get_temp_dir() . '/phpstan' (usually /tmp/phpstan or %TEMP%\phpstan)
 * - Custom: configured via tmpDir parameter in a config file
 * 
 * This provider excludes these cache directories from indexing when they're within the project.
 */
class PhpStanExclusionProvider : FrameworkExclusionProvider {
  companion object {
    private val LOG = Logger.getInstance(PhpStanExclusionProvider::class.java)
  }

  override val frameworkName: String = "PHPStan"

  override fun getExclusionDirectories(project: Project): List<String> {
    val projectDir = project.guessProjectDir() ?: return emptyList()
    val tmpDir = findPhpStanConfig(project)?.let { config ->
      parseTmpDir(config)
    } ?: return emptyList()

    val relativePath = tmpDir.relativizeTmpDir(projectDir.path) ?: return emptyList()
    return listOf(relativePath)
  }

  private fun findPhpStanConfig(project: Project): VirtualFile? {
    val configNames = listOf(
      "phpstan.neon",
      "phpstan.neon.dist",
      "phpstan.dist.neon",
    )

    val projectDir = project.guessProjectDir() ?: return null
    for (configName in configNames) {
      val configFile = projectDir.findChild(configName)
      if (configFile?.exists() == true) {
        return configFile
      }
    }
    return null
  }

  private fun parseTmpDir(configFile: VirtualFile): String? {
    return try {
      configFile.inputStream.use { stream ->
        val content = stream.reader().readText().replace("\t", " ".repeat(4))
        val data = Yaml().load<Any>(content) as? Map<*, *> ?: return null
        val parameters = data["parameters"] as? Map<*, *> ?: return null
        parameters["tmpDir"] as? String
      }
    }
    catch (e: Exception) {
      LOG.warn("Failed to parse PHPStan config file ${configFile.path}", e)
      null
    }
  }
}

/**
 * Returns PHPStan's `tmpDir` as a directory path relative to [basePath], or `null` when it does not denote a directory
 * strictly inside it.
 */
internal fun String.relativizeTmpDir(basePath: String): String? {
  val trimmed = trim()
  // Placeholders such as %rootDir%, %currentWorkingDirectory%, or %TMP% are expanded by PHPStan, not by the IDE.
  if (trimmed.isEmpty() || trimmed.contains('%')) return null
  if (OSAgnosticPathUtil.isAbsolute(trimmed) ||
      OSAgnosticPathUtil.startsWithWindowsDrive(trimmed) ||
      PathUtilRt.isSeparator(trimmed.first())) {
    return null
  }

  val resolved = FileUtil.toCanonicalPath("$basePath/$trimmed", '\\')
  if (!FileUtil.isAncestor(basePath, resolved, true)) return null

  return FileUtil.getRelativePath(basePath, resolved, '/')
}

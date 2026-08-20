package com.jetbrains.php.tools.quality.phpstan.exclusion

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PhpStanTmpDirNormalizationTest {
  private fun normalize(path: String) = path.relativizeTmpDir(BASE)

  @Test
  fun `plain relative paths are kept`() {
    assertEquals("cache", normalize("cache"))
    assertEquals("var/cache", normalize("var/cache"))
    assertEquals(".phpstan-cache", normalize(".phpstan-cache"))
  }

  @Test
  fun `redundant segments are stripped`() {
    assertEquals("cache", normalize("./cache"))
    assertEquals("cache", normalize("cache/"))
    assertEquals("var/cache", normalize("./var//cache/"))
    assertEquals("cache", normalize("  cache  "))
  }

  @Test
  fun `paths that collapse to the project root are rejected`() {
    for (path in listOf("", "   ", ".", "./", "/", "//", "/.", "./.")) {
      assertNull("'$path' must not resolve to the project root", normalize(path))
    }
  }

  @Test
  fun `windows separators are treated as separators on any host`() {
    assertEquals("cache/phpstan", normalize("""cache\phpstan"""))
    assertEquals("tools/cache/phpstan", normalize("""tools\cache/phpstan"""))
    assertEquals("cache", normalize("""cache\"""))
    assertEquals("cache", normalize(""".\cache"""))
  }

  @Test
  fun `parent references are rejected whichever separator is used`() {
    assertNull(normalize("""..\cache"""))
    assertNull(normalize("""cache\..\.."""))
    assertEquals("cache", normalize("""tools\..\cache"""))
  }

  @Test
  fun `drive relative and root relative paths are rejected`() {
    assertNull(normalize("C:cache"))
    assertNull(normalize("""\tmp\phpstan"""))
    assertNull(normalize("""\"""))
  }

  @Test
  fun `absolute paths are rejected`() {
    assertNull(normalize("/tmp/phpstan"))
    assertNull(normalize("C:/Temp/phpstan"))
    assertNull(normalize("""C:\Temp\phpstan"""))
    assertNull(normalize("""\\wsl$\Debian\tmp"""))
  }

  @Test
  fun `placeholders and parent references are rejected`() {
    assertNull(normalize("%rootDir%/../cache"))
    assertNull(normalize("%TMP%/phpstan"))
    assertNull(normalize("cache/%rootDir%"))
    assertNull(normalize("../cache"))
    assertNull(normalize("cache/../.."))
  }

  private companion object {
    const val BASE: String = "/home/user/project"
  }
}

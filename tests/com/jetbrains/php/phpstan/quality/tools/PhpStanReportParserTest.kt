package com.jetbrains.php.phpstan.quality.tools

import com.jetbrains.php.tools.quality.ParsedProblem
import com.jetbrains.php.tools.quality.QualityToolMessage.Severity
import com.jetbrains.php.tools.quality.phpstan.PhpStanReportParser
import junit.framework.TestCase

class PhpStanReportParserTest : TestCase() {
  private val report = """
    <?xml version="1.0" encoding="UTF-8"?>
    <checkstyle>
      <file name="/src/a.php">
        <error line="3" column="5" severity="error" message="Undefined variable: ${'$'}x"/>
        <error line="7" severity="warning" message="Dead code"/>
      </file>
      <file name="/src/b.php">
        <error line="1" column="1" severity="error" message="Other file"/>
      </file>
    </checkstyle>
  """.trimIndent()

  fun testAllFiles() {
    assertEquals(
      listOf(
        ParsedProblem(Severity.ERROR, 3, 4, "Undefined variable: \$x", "/src/a.php"),
        ParsedProblem(Severity.WARNING, 7, 0, "Dead code", "/src/a.php"),
        ParsedProblem(Severity.ERROR, 1, 0, "Other file", "/src/b.php"),
      ),
      PhpStanReportParser().parse(report),
    )
  }

  fun testOneFile() {
    val problems = PhpStanReportParser("/project/src/b.php").parse(report)
    assertEquals(listOf("Other file"), problems.map { it.message })
  }

  fun testEditorModeFile() {
    val problems = PhpStanReportParser("/tmp/copy.php", "/src/a.php").parse(report)
    assertEquals(2, problems.size)
  }

  fun testMalformedReportKeepsEarlierProblems() {
    val problems = PhpStanReportParser().parse(report.substringBefore("</file>") + "<broken")
    assertEquals(2, problems.size)
  }

  fun testProblemWithoutLineIsSkipped() {
    val problems = PhpStanReportParser().parse("""<file name="a.php"><error severity="error" message="m"/></file>""")
    assertTrue(problems.isEmpty())
  }
}

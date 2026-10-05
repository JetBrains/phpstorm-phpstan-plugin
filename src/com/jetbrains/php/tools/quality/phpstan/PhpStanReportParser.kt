package com.jetbrains.php.tools.quality.phpstan

import com.intellij.openapi.diagnostic.logger
import com.intellij.util.PathUtil
import com.jetbrains.php.tools.quality.ParsedProblem
import com.jetbrains.php.tools.quality.QualityToolMessage.Severity
import com.jetbrains.php.tools.quality.ReportParser
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.io.StringReader
import javax.xml.parsers.SAXParserFactory

private val LOG = logger<PhpStanReportParser>()

/**
 * Parses the PHPStan XML report.
 *
 * The parser keeps only the problems of the files that [matchesFile] accepts.
 * When [filePath] is null, the parser keeps the problems of every file.
 */
class PhpStanReportParser(
  private val filePath: String? = null,
  private val editorModeFilePath: String? = null,
) : ReportParser {

  override fun parse(report: CharSequence): List<ParsedProblem> {
    val handler = Handler()
    try {
      SAXParserFactory.newDefaultInstance().newSAXParser().parse(InputSource(StringReader(report.toString())), handler)
    }
    catch (e: SAXException) {
      LOG.debug("Malformed PHPStan report", e)
    }
    return handler.problems
  }

  private inner class Handler : DefaultHandler() {
    val problems = ArrayList<ParsedProblem>()
    private var fileAttr: String? = null
    private var isFileMatched = false

    override fun startElement(uri: String?, localName: String?, qName: String, attributes: Attributes) {
      when (qName) {
        FILE_TAG -> {
          val name = PathUtil.toSystemIndependentName(attributes.getValue(FILE_NAME_ATTR) ?: "")
          fileAttr = name
          isFileMatched = matchesFile(filePath, editorModeFilePath, name)
        }
        ERROR_TAG, WARNING_TAG -> if (isFileMatched) addProblem(attributes)
      }
    }

    private fun addProblem(attributes: Attributes) {
      val line = attributes.getValue(LINE_NUMBER_ATTR)?.toIntOrNull()
      val message = attributes.getValue(MESSAGE_ATTR)
      if (line == null || line < 0 || message == null) {
        LOG.debug("Skipped a PHPStan problem without a valid line or message")
        return
      }
      val column = attributes.getValue(COLUMN_NUMBER_ATTR)?.toIntOrNull() ?: 0
      val severity = if (attributes.getValue(SEVERITY_ATTR) == ERROR_TAG) Severity.ERROR else Severity.WARNING
      problems.add(ParsedProblem(severity, line, maxOf(0, column - 1), message, fileAttr))
    }
  }

  companion object {
    private const val ERROR_TAG = "error"
    private const val FILE_TAG = "file"
    private const val WARNING_TAG = "warning"
    private const val LINE_NUMBER_ATTR = "line"
    private const val COLUMN_NUMBER_ATTR = "column"
    private const val MESSAGE_ATTR = "message"
    private const val SEVERITY_ATTR = "severity"
    private const val FILE_NAME_ATTR = "name"

    @JvmStatic
    fun matchesFile(filePath: String?, editorModeFilePath: String?, fileAttr: String?): Boolean {
      if (filePath == null) return true
      val attr = fileAttr ?: ""
      if (filePath.endsWith(attr)) return true
      return editorModeFilePath != null && editorModeFilePath.endsWith(attr)
    }
  }
}

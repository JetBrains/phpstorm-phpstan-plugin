package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.Trinity;
import com.jetbrains.php.tools.quality.QualityToolAnnotatorInfo;
import com.jetbrains.php.tools.quality.QualityToolMessage;
import com.jetbrains.php.tools.quality.QualityToolXmlMessageProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jetbrains.php.tools.quality.QualityToolMessage.Severity.ERROR;
import static com.jetbrains.php.tools.quality.QualityToolMessage.Severity.WARNING;

public class PhpStanMessageProcessor extends QualityToolXmlMessageProcessor {
  private static final String PHP_STAN = "PHPStan";
  private final static String ERROR_TAG = "error";
  private final static String FILE_TAG = "file";
  private final static String WARNING_MESSAGE_START = "<file";
  private final static String WARNING_MESSAGE_END = "</file>";
  private final static String WARNING_TAG = "warning";
  private final static String LINE_NUMBER_ATTR = "line";
  private final static String MESSAGE_ATTR = "message";
  private final static String SEVERITY_ATTR = "severity";
  private static final Logger LOG = Logger.getInstance(QualityToolXmlMessageProcessor.class);
  private final Set<Trinity<Integer, String, QualityToolMessage.Severity>> lineMessages = new HashSet<>();
  private final HighlightDisplayLevel myWarningsHighlightLevel;

  protected PhpStanMessageProcessor(QualityToolAnnotatorInfo info, int maxMessages) {
    super(info, maxMessages);
    myWarningsHighlightLevel = HighlightDisplayLevel.WARNING; // TODO: fix
  }

  @Override
  protected void processMessage(InputSource source) throws SAXException, IOException {
    PhpStanXmlMessageHandler messageHandler = (PhpStanXmlMessageHandler)getXmlMessageHandler();
    mySAXParser.parse(source, messageHandler);
    if (messageHandler.isStatusValid()) {
      for (Trinity<Integer, String, QualityToolMessage.Severity> problem : messageHandler.getProblemList()) {
        QualityToolMessage qualityToolMessage =
          new QualityToolMessage(this, problem.first, problem.third, problem.second);
        if (!lineMessages.contains(problem)) {
          lineMessages.add(problem);
          addMessage(qualityToolMessage);
        }
      }
    }
  }

  @Override
  protected XMLMessageHandler getXmlMessageHandler() {
    return new PhpStanXmlMessageHandler();
  }

  @Override
  public int getMessageStart(@NotNull String line) {
    return line.indexOf(WARNING_MESSAGE_START);
  }

  @Override
  public int getMessageEnd(@NotNull String line) {
    return line.indexOf(WARNING_MESSAGE_END);
  }

  @Nullable
  @Override
  protected String getMessagePrefix() {
    return "phpstan";
  }

  @Nullable
  @Override
  protected HighlightDisplayLevel severityToDisplayLevel(@NotNull QualityToolMessage.Severity severity) {
    return WARNING.equals(severity) ? myWarningsHighlightLevel : null;
  }

  @Override
  public boolean processStdErrMessages() {
    return false;
  }

  @NotNull
  @Override
  protected String getQuickFixFamilyName() {
    return "PHPStan";
  }

  @Override
  protected Configurable getToolConfigurable(@NotNull Project project) {
    return new PhpStanConfigurable(project);
  }

  private class PhpStanXmlMessageHandler extends XMLMessageHandler {
    private List<Trinity<Integer, String, QualityToolMessage.Severity>> myProblemList;

    private List<Trinity<Integer, String, QualityToolMessage.Severity>> getProblemList() {
      return myProblemList;
    }
    
    @Override
    protected void parseTag(@NotNull String tagName, @NotNull Attributes attributes) {
      if (FILE_TAG.equals(tagName)) {
        myProblemList = new ArrayList<>();
      }
      if (ERROR_TAG.equals(tagName) | WARNING_TAG.equals(tagName)) {
        final String severity = attributes.getValue(SEVERITY_ATTR);
        mySeverity = severity.equals(ERROR_TAG) ? ERROR : WARNING;
        myLineNumber = parseLineNumber(attributes.getValue(LINE_NUMBER_ATTR));
        myProblemList.add(Trinity.create(myLineNumber, attributes.getValue(MESSAGE_ATTR), mySeverity));
      }
    }
  }
}
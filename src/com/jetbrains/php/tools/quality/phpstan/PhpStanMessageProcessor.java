package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.jetbrains.php.tools.quality.QualityToolAnnotatorInfo;
import com.jetbrains.php.tools.quality.QualityToolMessage;
import com.jetbrains.php.tools.quality.QualityToolXmlMessageProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.Attributes;

import java.util.HashSet;
import java.util.Set;

import static com.jetbrains.php.tools.quality.QualityToolMessage.Severity.WARNING;

public class PhpStanMessageProcessor extends QualityToolXmlMessageProcessor {
  private static final String PHP_STAN = "PHPStan";
  private final static String ERROR_MESSAGE_START = "<error";
  private final static String ERROR_MESSAGE_END = "</error>";
  private final static String ERROR_TAG = "error";
  private final static String WARNING_MESSAGE_START = "<warning";
  private final static String WARNING_MESSAGE_END = "</warning>";
  private final static String WARNING_TAG = "warning";
  private final static String LINE_NUMBER_ATTR = "line";
  private final static String SNIFF_NAME_ATTR = "source";
  private static final Logger LOG = Logger.getInstance(QualityToolXmlMessageProcessor.class);
  private final Set<TextRange> lineMessages = new HashSet<>();
  private final HighlightDisplayLevel myWarningsHighlightLevel;

  protected PhpStanMessageProcessor(QualityToolAnnotatorInfo info, int maxMessages) {
    super(info, maxMessages);
    myWarningsHighlightLevel = HighlightDisplayLevel.WARNING; // TODO: fix
  }

  @Override
  protected XMLMessageHandler getXmlMessageHandler() {
    return new PhpStanXmlMessageHandler();
  }

  @Override
  public int getMessageStart(@NotNull String line) {
    int messageStart = line.indexOf(ERROR_MESSAGE_START);
    if (messageStart < 0) {
      messageStart = line.indexOf(WARNING_MESSAGE_START);
    }
    return messageStart;
  }

  @Override
  public int getMessageEnd(@NotNull String line) {
    int messageEnd = line.indexOf(ERROR_MESSAGE_END);
    if (messageEnd < 0) {
      messageEnd = line.indexOf(WARNING_MESSAGE_END);
    }
    return messageEnd;
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
    @Override
    protected void parseTag(@NotNull String tagName, @NotNull Attributes attributes) {
      if (ERROR_TAG.equals(tagName)) {
        mySeverity = WARNING;
      }
      else if (WARNING_TAG.equals(tagName)) {
        mySeverity = WARNING;
      }
      myLineNumber = parseLineNumber(attributes.getValue(LINE_NUMBER_ATTR));
    }
  }
}
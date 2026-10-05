package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.tools.quality.QualityToolAnnotatorInfo;
import com.jetbrains.php.tools.quality.QualityToolMessage;
import com.jetbrains.php.tools.quality.ParsedProblem;
import com.jetbrains.php.tools.quality.QualityToolXmlMessageProcessor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.jetbrains.php.tools.quality.QualityToolMessage.Severity.WARNING;
import static com.jetbrains.php.tools.quality.phpstan.PhpStanGlobalInspection.PHPSTAN_ANNOTATOR_INFO;

public class PhpStanMessageProcessor extends QualityToolXmlMessageProcessor {
  private static final @NonNls String WARNING_MESSAGE_START = "<file";
  private static final @NonNls String WARNING_MESSAGE_END = "</file>";
  final String myFilePath;
  final @Nullable String myEditorModeFilePath;
  final PsiFile myPsiFile;
  final Project myProject;

  protected PhpStanMessageProcessor(QualityToolAnnotatorInfo<?> info) {
    this(info, null);
  }

  protected PhpStanMessageProcessor(QualityToolAnnotatorInfo<?> info, @Nullable String editorModeFilePath) {
    super(info);
    myFilePath = info.getTempFilePath();
    myEditorModeFilePath = editorModeFilePath;
    myPsiFile = info.getPsiFile();
    myProject = info.getProject();
  }

  @Override
  protected boolean show(@NotNull String message) {
    return !message.contains("The Xdebug PHP extension is active, but \"--xdebug\" is not used");
  }

  @Override
  protected void processMessage(@NotNull String message) {
    final List<ParsedProblem> problems = new PhpStanReportParser(myFilePath, myEditorModeFilePath).parse(message);
    if (myPsiFile != null) {
      addParsedProblems(problems);
    }
    else {
      appendProblems(myProject, PHPSTAN_ANNOTATOR_INFO, problems);
    }
  }

  @Override
  public int getMessageStart(@NotNull String line) {
    return line.indexOf(WARNING_MESSAGE_START);
  }

  @Override
  public int getMessageEnd(@NotNull String line) {
    return line.indexOf(WARNING_MESSAGE_END);
  }

  @Override
  protected @NonNls @Nullable String getMessagePrefix() {
    return "phpstan";
  }

  @Override
  protected @Nullable HighlightDisplayLevel severityToDisplayLevel(@NotNull QualityToolMessage.Severity severity) {
    return WARNING.equals(severity) ? HighlightDisplayLevel.WARNING : null;
  }

  @Override
  protected PhpStanQualityToolType getQualityToolType() {
    return PhpStanQualityToolType.INSTANCE;
  }
}

package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.PathUtil;
import com.jetbrains.php.tools.quality.QualityToolAnnotatorInfo;
import com.jetbrains.php.tools.quality.QualityToolMessage;
import com.jetbrains.php.tools.quality.QualityToolXmlMessageProcessor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jetbrains.php.tools.quality.QualityToolMessage.Severity.WARNING;
import static com.jetbrains.php.tools.quality.phpstan.PhpStanGlobalInspection.PHPSTAN_ANNOTATOR_INFO;

public class PhpStanMessageProcessor extends QualityToolXmlMessageProcessor {
  private static final @NonNls String WARNING_MESSAGE_START = "<file";
  private static final @NonNls String WARNING_MESSAGE_END = "</file>";
  private final Set<ProblemDescription> lineMessages = new HashSet<>();
  private final HighlightDisplayLevel myWarningsHighlightLevel;
  final String myFilePath;
  final @Nullable String myEditorModeFilePath;
  final PsiFile myPsiFile;
  final Project myProject;

  protected PhpStanMessageProcessor(QualityToolAnnotatorInfo<?> info) {
    this(info, null);
  }

  protected PhpStanMessageProcessor(QualityToolAnnotatorInfo<?> info, @Nullable String editorModeFilePath) {
    super(info);
    myWarningsHighlightLevel = HighlightDisplayLevel.WARNING; // TODO: fix
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
  protected void processMessage(InputSource source) throws SAXException, IOException {
    PhpStanXmlMessageHandler messageHandler = (PhpStanXmlMessageHandler)getXmlMessageHandler(myFilePath);
    mySAXParser.parse(source, messageHandler);
    if (messageHandler.isStatusValid()) {
      if (myPsiFile != null) {
        List<ProblemDescription> list = messageHandler.getProblemList();
        if (list == null) return;
        for (ProblemDescription problem : list) {
          if (myProject.isDisposed()) return;
          Document document = ReadAction.compute(() -> PsiDocumentManager.getInstance(myPsiFile.getProject()).getDocument(myPsiFile));
          QualityToolMessage qualityToolMessage;
          if (document != null && problem.getLineNumber() - 1 > 0 && problem.getLineNumber() - 1 < document.getLineCount()) {
            qualityToolMessage = new QualityToolMessage(this, TextRange
              .create(document.getLineStartOffset(problem.getLineNumber() - 1) + problem.getColumn(),
                      document.getLineEndOffset(problem.getLineNumber() - 1)), problem.getSeverity(), problem.getMessage());
          }
          else {
            qualityToolMessage = new QualityToolMessage(this, problem.getLineNumber(), problem.getSeverity(), problem.getMessage());
          }
          if (lineMessages.add(problem)) {
            addMessage(qualityToolMessage);
          }
        }
      } else {
        appendProblems(myProject, PHPSTAN_ANNOTATOR_INFO, messageHandler.getProblemList());
      }
    }
  }

  @Override
  protected XMLMessageHandler getXmlMessageHandler() {
    return null;
  }

  protected XMLMessageHandler getXmlMessageHandler(@Nullable String filePath) {
    return new PhpStanXmlMessageHandler(filePath, myEditorModeFilePath);
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
    return WARNING.equals(severity) ? myWarningsHighlightLevel : null;
  }

  @Override
  protected PhpStanQualityToolType getQualityToolType() {
    return PhpStanQualityToolType.INSTANCE;
  }

  public static boolean matchesFile(@Nullable String filePath, @Nullable String editorModeFilePath, @Nullable String fileAttr) {
    if (filePath == null) return true;
    String attr = fileAttr == null ? "" : fileAttr;
    if (filePath.endsWith(attr)) return true;
    return editorModeFilePath != null && editorModeFilePath.endsWith(attr);
  }

  static final class PhpStanXmlMessageHandler extends CheckstyleXmlMessageHandler {

    private final String myFilePath;
    private final @Nullable String myEditorModeFilePath;
    private String myFileAttr;

    PhpStanXmlMessageHandler(@Nullable String filePath, @Nullable String editorModeFilePath) {
      myFilePath = filePath;
      myEditorModeFilePath = editorModeFilePath;
    }

    private List<ProblemDescription> myProblemList;

    private List<ProblemDescription> getProblemList() {
      return myProblemList;
    }

    @Override
    protected void onFile(@Nullable String fileName) {
      myFileAttr = PathUtil.toSystemIndependentName(fileName);
      myProblemList = matchesFile(myFilePath, myEditorModeFilePath, myFileAttr) ? new ArrayList<>() : null;
    }

    @Override
    protected void onMessage(@NotNull QualityToolMessage.Severity severity, int line, int column, @Nullable String message) {
      if (myProblemList != null) {
        mySeverity = severity;
        myLineNumber = line;
        myProblemList.add(new ProblemDescription(severity, line, Math.max(0, column - 1), message, myFileAttr));
      }
    }
  }
}
package com.jetbrains.php.phpstan.lang.documentation;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocCommentBase;
import com.intellij.testFramework.junit5.TestApplication;
import com.intellij.testFramework.junit5.fixture.TestFixture;
import com.intellij.util.CollectConsumer;
import com.jetbrains.php.fixtures.PhpCodeInsightTestFixture;
import com.jetbrains.php.fixtures.PhpTestDataLayout;
import com.jetbrains.php.lang.documentation.PhpDocumentationProvider;
import com.jetbrains.php.phpstan.lang.documentation.parser.PhpStanDocParserTest;
import org.junit.jupiter.api.Test;

import static com.jetbrains.php.fixtures.PhpCodeInsightTestFixture.phpCodeInsightFixture;
import static com.jetbrains.php.fixtures.PhpTestFixtures.phpProjectFixture;

@TestApplication
public class PhpStanDocumentationProviderTest {
  private static final TestFixture<Project> projectFixture = phpProjectFixture();
  private final TestFixture<PhpCodeInsightTestFixture> php = phpCodeInsightFixture(
    projectFixture,
    new PhpTestDataLayout("codeInsight/documentationProvider").withHome(PhpStanDocParserTest.TEST_DATA_HOME).withExtensions("php", "html")
  );

  public void doTestRenderedDoc() {
    php.get().configureByFile();
    String res = ReadAction.computeBlocking(() -> {
      PhpDocumentationProvider docProvider = new PhpDocumentationProvider();
      CollectConsumer<PsiDocCommentBase> docComments = new CollectConsumer<>();
      PhpDocumentationProvider provider = new PhpDocumentationProvider();
      provider.collectDocComments(php.get().getFile(), docComments);
      StringBuilder builder = new StringBuilder();
      docComments.getResult().forEach(c -> {
        builder.append(docProvider.generateRenderedDoc(c));
        builder.append("\n");
      });
      return builder.toString().replace("<br>", "<br>\n").replace("<p>", "\n<p>").replace("<table>", "\n<table>").replace("<tr>", "\n<tr>");
    });
    php.get().configurePhpByText("result.html", res);
    php.get().checkResultByFile();
  }

  @Test
  public void testPhpStanTags() {
    doTestRenderedDoc();
  }

  @Test
  public void testVarTag() {
    doTestRenderedDoc();
  }

  @Test
  public void testCustomPhpStanTags() {
    doTestRenderedDoc();
  }
}

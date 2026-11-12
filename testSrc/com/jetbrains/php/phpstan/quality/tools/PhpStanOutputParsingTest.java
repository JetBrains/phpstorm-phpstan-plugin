package com.jetbrains.php.phpstan.quality.tools;

import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.junit5.TestApplication;
import com.intellij.testFramework.junit5.fixture.TestFixture;
import com.jetbrains.php.fixtures.PhpHeavyCodeInsightTestFixture;
import com.jetbrains.php.fixtures.PhpTestDataLayout;
import com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurationManager;
import com.jetbrains.php.tools.quality.phpstan.PhpStanGlobalInspection;
import org.junit.jupiter.api.Test;

import static com.jetbrains.php.fixtures.PhpHeavyCodeInsightTestFixture.phpHeavyCodeInsightFixture;

@TestApplication
public class PhpStanOutputParsingTest {
  private final TestFixture<PhpHeavyCodeInsightTestFixture> php =
    phpHeavyCodeInsightFixture(new PhpTestDataLayout("output").withHome("/phpstorm/phpstan/testData/").withoutTestDir());

  @Test
  public void testSimple() {
    CodeInsightTestFixture myFixture = php.get().getFixture();
    PhpStanConfigurationManager.getInstance(myFixture.getProject()).getOrCreateLocalSettings().setToolPath("phpstan"); // Dummy, needed to run annotator
    php.get().configureByFiles(php.get().getFileBeforeRelativePath().replace(".php", ".txt"));
    myFixture.enableInspections(new PhpStanGlobalInspection());
    myFixture.testHighlighting(true, false, true);
  }
}

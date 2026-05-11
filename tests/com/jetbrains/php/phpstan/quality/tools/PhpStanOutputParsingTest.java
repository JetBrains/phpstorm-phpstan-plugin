package com.jetbrains.php.phpstan.quality.tools;

import com.jetbrains.php.fixtures.PhpHeavyCodeInsightFixtureTestCase;
import com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurationManager;
import com.jetbrains.php.tools.quality.phpstan.PhpStanGlobalInspection;

public class PhpStanOutputParsingTest extends PhpHeavyCodeInsightFixtureTestCase {
 
  public void testSimple() {
    PhpStanConfigurationManager.getInstance(myFixture.getProject()).getOrCreateLocalSettings().setToolPath("phpstan"); // Dummy, needed to run annotator
    configureByFiles(getFileBeforeRelativePath().replace(".php", ".txt"));
    myFixture.enableInspections(new PhpStanGlobalInspection());
    myFixture.testHighlighting(true, false, true);
  }

  @Override
  protected String getFixtureTestDataFolder() {
    return "output";
  }

  @Override
  protected String getBasePath() {
    return "/phpstorm/phpstan/testData/output";
  }
}

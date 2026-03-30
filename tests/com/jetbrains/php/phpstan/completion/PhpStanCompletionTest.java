package com.jetbrains.php.phpstan.completion;

import com.intellij.testFramework.TestFrameworkUtil;
import com.intellij.testFramework.TestIndexingModeSupporter;
import com.jetbrains.php.fixtures.PhpCompletionTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.jetbrains.annotations.NotNull;

public class PhpStanCompletionTest extends PhpCompletionTestCase {

  @Override
  protected @NotNull String getTestDataHome() {
    return "/phpstorm/phpstan/testData/";
  }

  @Override
  protected String getFixtureTestDataFolder() {
    return "completion";
  }

  public void testDocTagCompletions() {
    doTestLookupElementsContains("phpstan-require-extends", "phpstan-require-implements");
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(PhpStanCompletionTest.class);
    TestIndexingModeSupporter.addAllTests(PhpStanCompletionTest.class, suite);
    return TestFrameworkUtil.flattenSuite(suite);
  }
}

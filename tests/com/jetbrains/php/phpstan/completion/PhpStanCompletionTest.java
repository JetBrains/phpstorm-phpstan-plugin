package com.jetbrains.php.phpstan.completion;

import com.jetbrains.php.fixtures.PhpCompletionTestCase;
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

}

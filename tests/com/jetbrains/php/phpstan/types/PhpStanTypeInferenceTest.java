package com.jetbrains.php.phpstan.types;

import com.jetbrains.php.codeInsight.PhpTypeInferenceTestCase;
import com.jetbrains.php.phpstan.lang.documentation.parser.PhpStanDocParserTest;
import org.jetbrains.annotations.NotNull;

public class PhpStanTypeInferenceTest extends PhpTypeInferenceTestCase {
  @Override
  protected @NotNull String getTestDataHome() {
    return PhpStanDocParserTest.TEST_DATA_HOME;
  }

  @Override
  protected String getFixtureTestDataFolder() {
    return "codeInsight/typeInference";
  }

  public void testDocTags() {
    doTypeTest();
  }

  public void testTemplates() {
    doTypeTest();
  }
}

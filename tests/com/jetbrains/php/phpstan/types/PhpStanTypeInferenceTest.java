package com.jetbrains.php.phpstan.types;

import com.jetbrains.php.codeInsight.PhpTypeInferenceTestCase;
import com.jetbrains.php.config.PhpLanguageLevel;
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

  public void testAliasImportType() {
    doTypeTest();
  }

  public void testConditionalType() {
    doTypeTest();
  }

  public void testNestedConditionalTypes() {
    doTypeTest();
  }

  public void testConditionalTypesWithGenerics() {
    doTypeTest();
  }

  public void testValueOfBackedEnumInParamArray() {
    doLanguageLevelTest(getProject(), PhpLanguageLevel.PHP810, () -> doTypeTest());
  }

  public void testValueOfBackedEnumInReturnArray() {
    doLanguageLevelTest(getProject(), PhpLanguageLevel.PHP810, () -> doTypeTest());
  }
}

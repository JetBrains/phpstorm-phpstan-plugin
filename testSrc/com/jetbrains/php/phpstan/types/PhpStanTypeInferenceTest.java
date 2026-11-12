package com.jetbrains.php.phpstan.types;

import com.intellij.openapi.project.Project;
import com.intellij.testFramework.junit5.TestApplication;
import com.intellij.testFramework.junit5.fixture.TestFixture;
import com.jetbrains.php.codeInsight.PhpTypeInferenceTestFixture;
import com.jetbrains.php.config.PhpLanguageLevel;
import com.jetbrains.php.fixtures.PhpTestDataLayout;
import com.jetbrains.php.phpstan.lang.documentation.parser.PhpStanDocParserTest;
import org.junit.jupiter.api.Test;

import static com.jetbrains.php.codeInsight.PhpTypeInferenceTestFixture.phpTypeInferenceFixture;
import static com.jetbrains.php.fixtures.PhpCodeInsightTestFixture.doLanguageLevelTest;
import static com.jetbrains.php.fixtures.PhpTestFixtures.phpProjectFixture;

@TestApplication
public class PhpStanTypeInferenceTest {
  private static final TestFixture<Project> projectFixture = phpProjectFixture();
  private final TestFixture<PhpTypeInferenceTestFixture> php =
    phpTypeInferenceFixture(projectFixture, new PhpTestDataLayout("codeInsight/typeInference").withHome(PhpStanDocParserTest.TEST_DATA_HOME));

  @Test
  public void testDocTags() {
    php.get().doTypeTest();
  }

  @Test
  public void testTemplates() {
    php.get().doTypeTest();
  }

  @Test
  public void testAliasImportType() {
    php.get().doTypeTest();
  }

  @Test
  public void testConditionalType() {
    php.get().doTypeTest();
  }

  @Test
  public void testNestedConditionalTypes() {
    php.get().doTypeTest();
  }

  @Test
  public void testConditionalTypesWithGenerics() {
    php.get().doTypeTest();
  }

  @Test
  public void testValueOfBackedEnumInParamArray() {
    doLanguageLevelTest(php.get().getProject(), PhpLanguageLevel.PHP810, () -> php.get().doTypeTest());
  }

  @Test
  public void testValueOfBackedEnumInReturnArray() {
    doLanguageLevelTest(php.get().getProject(), PhpLanguageLevel.PHP810, () -> php.get().doTypeTest());
  }
}

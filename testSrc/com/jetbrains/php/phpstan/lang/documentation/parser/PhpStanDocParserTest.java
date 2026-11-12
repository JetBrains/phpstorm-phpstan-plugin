package com.jetbrains.php.phpstan.lang.documentation.parser;

import com.intellij.openapi.project.Project;
import com.intellij.testFramework.junit5.TestApplication;
import com.intellij.testFramework.junit5.fixture.TestFixture;
import com.jetbrains.php.lang.parser.PhpFileSetParserTestFixture;
import org.junit.jupiter.api.Test;

import static com.intellij.testFramework.junit5.fixture.FixturesKt.projectFixture;
import static com.jetbrains.php.lang.parser.PhpFileSetParserTestFixture.phpFileSetParserFixture;

@TestApplication
public class PhpStanDocParserTest {
  public static final String TEST_DATA_HOME = "/phpstorm/phpstan/testData/";

  private static final TestFixture<Project> projectFixture = projectFixture();

  private final TestFixture<PhpFileSetParserTestFixture> parser = phpFileSetParserFixture(projectFixture, TEST_DATA_HOME + "parser");

  @Test
  public void test$DocTags() {
    parser.get().doTest();
  }

  @Test
  public void test$DocMethodsTags() {
    parser.get().doTest();
  }

  @Test
  public void test$Template() {
    parser.get().doTest();
  }

  @Test
  public void test$Assert() {
    parser.get().doTest();
  }

  @Test
  public void test$Inheritance() {
    parser.get().doTest();
  }

  @Test
  public void test$Method() {
    parser.get().doTest();
  }

  @Test
  public void test$Type() {
    parser.get().doTest();
  }

  @Test
  public void test$InplaceCovariantGeneric() {
    parser.get().doTest();
  }

  @Test
  public void test$InplaceContravariantGeneric() {
    parser.get().doTest();
  }
}

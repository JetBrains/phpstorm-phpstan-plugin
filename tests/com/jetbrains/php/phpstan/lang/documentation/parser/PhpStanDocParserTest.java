package com.jetbrains.php.phpstan.lang.documentation.parser;

import com.jetbrains.php.lang.parser.BasicPhpParserTestCase;
import org.jetbrains.annotations.NotNull;

public class PhpStanDocParserTest extends BasicPhpParserTestCase {
  public static final String TEST_DATA_HOME = "/phpstorm/phpstan/testData/";

  @Override
  protected String getDataPath() {
    return TEST_DATA_HOME + "parser";
  }

  public void test$DocTags() throws Throwable {
    doTest();
  }

  public void test$DocMethodsTags() throws Throwable {
    doTest();
  }

  public void test$Template() throws Throwable {
    doTest();
  }

  public void test$Assert() throws Throwable {
    doTest();
  }

  public void test$Inheritance() throws Throwable {
    doTest();
  }

  public void test$Method() throws Throwable {
    doTest();
  }

  public void test$Type() throws Throwable {
    doTest();
  }

  public void test$InplaceCovariantGeneric() throws Throwable {
    doTest();
  }

  public void test$InplaceContravariantGeneric() throws Throwable {
    doTest();
  }
}

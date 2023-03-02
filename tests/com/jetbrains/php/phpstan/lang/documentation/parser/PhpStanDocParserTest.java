package com.jetbrains.php.phpstan.lang.documentation.parser;

import com.jetbrains.php.lang.parser.PhpParserTestCase;
import org.jetbrains.annotations.NotNull;

public class PhpStanDocParserTest extends PhpParserTestCase {
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
}

package com.jetbrains.php.phpstan.lang.documentation.parser;

import com.jetbrains.php.lang.parser.PhpParserTestCase;

public class PhpStanDocParserTest extends PhpParserTestCase {

  @Override
  protected String getDataPath() {
    return "/phpstorm/phpstan/testData/parser";
  }

  public void test$DocTags() throws Throwable {
    doTest();
  }
}

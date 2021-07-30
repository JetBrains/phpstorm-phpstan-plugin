package com.jetbrains.php.phpstan.lang.documentation.parser;

import com.jetbrains.php.lang.documentation.phpdoc.parser.tags.PhpDocTagParser;
import com.jetbrains.php.lang.parser.PhpPsiBuilder;

public class PhpStanExtendsTagParser extends PhpDocTagParser {
  @Override
  protected boolean parseContents(PhpPsiBuilder builder) {
    return parseTypes(builder);
  }
}

package com.jetbrains.php.phpstan.types;

import com.jetbrains.php.lang.psi.resolve.types.PhpCustomDocTagTypeProvider;
import org.jetbrains.annotations.NotNull;

public class PhpStanDocTypeProvider extends PhpCustomDocTagTypeProvider {

  @Override
  public char getKey() {
    return '\u9949';
  }
  @Override
  @NotNull
  public String getParamTag() {
    return "@phpstan-param";
  }

  @Override
  public @NotNull String getReturnTag() {
    return "@phpstan-return";
  }

  @Override
  public @NotNull String getVarTag() {
    return "@phpstan-var";
  }
}

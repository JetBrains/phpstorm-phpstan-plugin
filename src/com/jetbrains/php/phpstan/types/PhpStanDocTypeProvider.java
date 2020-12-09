package com.jetbrains.php.phpstan.types;

import com.jetbrains.php.lang.psi.resolve.types.PhpCustomDocTagTypeProvider;
import org.jetbrains.annotations.NotNull;

public class PhpStanDocTypeProvider extends PhpCustomDocTagTypeProvider {

  @Override
  public char getKey() {
    return '\u9949';
  }
  @Override
  protected @NotNull String getParamTag() {
    return "@phpstan-param";
  }

  @Override
  protected @NotNull String getReturnTag() {
    return "@phpstan-return";
  }

  @Override
  protected @NotNull String getVarTag() {
    return "@phpstan-var";
  }
}

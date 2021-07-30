package com.jetbrains.php.phpstan.types;

import com.jetbrains.php.lang.psi.resolve.types.PhpDocPrefixProvider;
import org.jetbrains.annotations.NotNull;

public class PhpStanDocPrefixProvider implements PhpDocPrefixProvider {
  @Override
  public @NotNull String getPrefix() {
    return "@phpstan-";
  }
}

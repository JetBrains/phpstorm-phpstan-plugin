package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;


public class PhpStanBundle {

  public static String message(@NotNull @PropertyKey(resourceBundle = PHP_BUNDLE) String key, @NotNull Object... params) {
    return CommonBundle.message(getBundle(), key, params);
  }

  private static Reference<ResourceBundle> ourBundle;
  @NonNls public static final String PHP_BUNDLE = "messages.PhpStanBundle";

  private PhpStanBundle() {
  }

  private static ResourceBundle getBundle() {
    ResourceBundle bundle = com.intellij.reference.SoftReference.dereference(ourBundle);
    if (bundle == null) {
      bundle = ResourceBundle.getBundle(PHP_BUNDLE);
      ourBundle = new SoftReference<>(bundle);
    }
    return bundle;
  }
}

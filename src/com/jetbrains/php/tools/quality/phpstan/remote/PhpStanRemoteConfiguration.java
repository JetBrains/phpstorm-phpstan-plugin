package com.jetbrains.php.tools.quality.phpstan.remote;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl;
import com.jetbrains.php.config.interpreters.PhpSdkDependentConfiguration;
import com.jetbrains.php.tools.quality.phpstan.PhpStanBundle;
import com.jetbrains.php.tools.quality.phpstan.PhpStanConfiguration;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.openapi.util.text.StringUtil.isEmpty;

@Tag("phpcs_fixer_by_interpreter")
public class PhpStanRemoteConfiguration extends PhpStanConfiguration implements PhpSdkDependentConfiguration {
  private String myInterpreterId;

  @Override
  @Nullable
  @Attribute("interpreter_id")
  public String getInterpreterId() {
    return myInterpreterId;
  }

  @Override
  public void setInterpreterId(@NotNull String interpreterId) {
    myInterpreterId = interpreterId;
  }

  @NotNull
  @Override
  public @NlsContexts.Label String getPresentableName(@Nullable Project project) {
    return getDefaultName(PhpInterpretersManagerImpl.getInstance(project).findInterpreterName(getInterpreterId()));
  }

  @NotNull
  @Override
  public @Nls String getId() {
    final String interpreterId = getInterpreterId();
    return isEmpty(interpreterId) ? PhpStanBundle.message("undefined.interpreter") : interpreterId;
  }

  @NotNull
  public static @Nls String getDefaultName(@Nullable String interpreterName) {
    return isEmpty(interpreterName) ? PhpStanBundle.message("undefined.interpreter") : PhpStanBundle.message("interpreter.0", interpreterName);
  }

  @Override
  public PhpStanRemoteConfiguration clone() {
    PhpStanRemoteConfiguration settings = new PhpStanRemoteConfiguration();
    settings.myInterpreterId = myInterpreterId;
    clone(settings);
    return settings;
  }

  @Override
  public String serialize(@Nullable String path) {
    return path;
  }

  @Override
  public String deserialize(@Nullable String path) {
    return path;
  }
}

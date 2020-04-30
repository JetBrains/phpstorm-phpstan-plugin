package com.jetbrains.php.tools.quality.phpstan.remote;

import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl;
import com.jetbrains.php.config.interpreters.PhpSdkDependentConfiguration;
import com.jetbrains.php.tools.quality.phpstan.PhpStanConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.openapi.util.text.StringUtil.isEmpty;

@Tag("phpcs_fixer_by_interpreter")
public class PhpStanRemoteConfiguration extends PhpStanConfiguration implements PhpSdkDependentConfiguration {
  private static final String UNDEFINED = "Undefined interpreter";
  private static final String INTERPRETER = "Interpreter: ";
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
  public String getPresentableName(@Nullable Project project) {
    return getDefaultName(PhpInterpretersManagerImpl.getInstance(project).findInterpreterName(getInterpreterId()));
  }

  @NotNull
  @Override
  public String getId() {
    final String interpreterId = getInterpreterId();
    return isEmpty(interpreterId) ? UNDEFINED : interpreterId;
  }

  @NotNull
  public static String getDefaultName(@Nullable String interpreterName) {
    return isEmpty(interpreterName) ? UNDEFINED : INTERPRETER + interpreterName;
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

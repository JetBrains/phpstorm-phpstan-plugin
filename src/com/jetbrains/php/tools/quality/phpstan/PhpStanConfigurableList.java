package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolConfigurableList;
import com.jetbrains.php.tools.quality.QualityToolType;
import com.jetbrains.php.ui.PhpUiUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhpStanConfigurableList extends QualityToolConfigurableList<PhpStanConfiguration> {
  @NonNls public static final String HELP_TOPIC = "reference.settings.php.PhpStan";
  @NonNls private static final String SUBJ_DISPLAY_NAME = "php_stan";

  public PhpStanConfigurableList(@NotNull final Project project, @Nullable String initialElement) {
    super(project, new PhpStanQualityToolType(), PhpStanConfiguration::new, PhpStanConfiguration::clone, initialElement);
    setSubjectDisplayName(SUBJ_DISPLAY_NAME);
  }

  @Override
  public String getHelpTopic() {
    return HELP_TOPIC;
  }

  @Override
  protected QualityToolType<PhpStanConfiguration> getQualityToolType() {
    return new PhpStanQualityToolType();
  }

  @NotNull
  public static Runnable createFix(@NotNull final Project project) {
    return () -> PhpUiUtil.editConfigurable(project, new PhpStanConfigurable(project));
  }
}
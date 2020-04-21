package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolConfigurableForm;
import com.jetbrains.php.tools.quality.QualityToolConfigurableList;
import com.jetbrains.php.tools.quality.QualityToolConfiguration;
import com.jetbrains.php.tools.quality.QualityToolConfigurationProvider;
import com.jetbrains.php.ui.PhpUiUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhpStanConfigurableList extends QualityToolConfigurableList<PhpStanConfiguration> {
  public static final String DISPLAY_NAME = "PHPStan";
  @NonNls public static final String HELP_TOPIC = "reference.settings.php.PhpStan";
  @NonNls private static final String SUBJ_DISPLAY_NAME = "php_stan";

  public PhpStanConfigurableList(@NotNull final Project project, @Nullable String initialElement) {
    super(project, PhpStanConfigurationManager.getInstance(project),
          PhpStanConfiguration::new,
          PhpStanConfiguration::clone,
          settings -> {
            final PhpStanConfigurationProvider provider = PhpStanConfigurationProvider.getInstances();
            if (provider != null) {
              final QualityToolConfigurableForm form = provider.createConfigurationForm(project, settings);
              if (form != null) {
                return form;
              }
            }
            return new PhpStanConfigurableForm<>(project, settings);
          },
          initialElement
    );
    setSubjectDisplayName(SUBJ_DISPLAY_NAME);
  }

  @Nullable
  @Override
  protected QualityToolConfigurationProvider<PhpStanConfiguration> getConfigurationProvider() {
    return PhpStanConfigurationProvider.getInstances();
  }

  @Override
  @Nullable
  protected PhpStanConfiguration getConfiguration(@Nullable QualityToolConfiguration configuration) {
    return configuration instanceof PhpStanConfiguration ? (PhpStanConfiguration)configuration : null;
  }

  @Nls
  @Override
  public String getDisplayName() {
    return DISPLAY_NAME;
  }

  @Override
  public String getHelpTopic() {
    return HELP_TOPIC;
  }

  @NotNull
  public static Runnable createFix(@NotNull final Project project) {
    return () -> PhpUiUtil.editConfigurable(project, new PhpStanConfigurable(project));
  }
}
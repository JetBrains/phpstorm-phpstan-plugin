package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolConfigurationComboBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.ActionListener;
import java.util.List;

public class PhpStanConfigurationComboBox extends QualityToolConfigurationComboBox<PhpStanConfiguration> {

  public PhpStanConfigurationComboBox(@Nullable Project project) {
    super(project);
  }

  @NotNull
  @Override
  protected ActionListener createBrowserAction(@NotNull final Project project) {
    return e -> {
      final QualityToolConfigurationItem item = getSelectedItem();
      final PhpStanConfigurableList configurableList = new PhpStanConfigurableList(project, item == null ? null : item.getName());
      editConfigurableList(configurableList, item);
    };
  }

  @Override
  @NotNull
  protected List<PhpStanConfiguration> getItems() {
    return PhpStanConfigurationManager.getInstance(myProject).getAllSettings();
  }

  @Override
  @NotNull
  protected PhpStanConfiguration getDefaultItem() {
    return PhpStanConfigurationManager.getInstance(myProject).getLocalSettings();
  }
}

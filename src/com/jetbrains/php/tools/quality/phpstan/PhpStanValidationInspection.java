package com.jetbrains.php.tools.quality.phpstan;

import com.jetbrains.php.tools.quality.QualityToolAnnotator;
import com.jetbrains.php.tools.quality.QualityToolValidationInspection;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.openapi.util.text.StringUtil.isNotEmpty;
import static com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurationBaseManager.PHP_STAN;

public class PhpStanValidationInspection extends QualityToolValidationInspection {
  public boolean FULL_PROJECT = false;
  @NonNls public String memoryLimit = "2G";
  public int level = 4;
  public String config = "";
  public String autoload = "";

  @Override
  public JComponent createOptionsPanel() {
    final PhpStanOptionsPanel optionsPanel = new PhpStanOptionsPanel(this);
    optionsPanel.init();
    return optionsPanel.getOptionsPanel();
  }

  @NotNull
  @Override
  protected QualityToolAnnotator getAnnotator() {
    return PhpStanAnnotatorProxy.INSTANCE;
  }

  @Override
  public String getToolName() {
    return PHP_STAN;
  }

  public List<String> getCommandLineOptions(List<String> filePath) {
    @NonNls ArrayList<String> options = new ArrayList<>();
    options.add("analyze");
    options.add("--level=" + level);
    if (isNotEmpty(config)) {
      options.add("--configuration=" + config);
    }
    if (isNotEmpty(autoload)) {
      options.add("--autoload-file=" + autoload);
    }
    options.add("--memory-limit=" + memoryLimit);
    options.add("--error-format=checkstyle");
    options.add("--no-progress");
    options.add("--no-ansi");
    options.addAll(filePath);
    return options;
  }
}

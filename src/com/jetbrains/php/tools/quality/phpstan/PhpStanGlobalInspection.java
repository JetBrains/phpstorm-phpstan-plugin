package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.php.tools.quality.QualityToolAnnotator;
import com.jetbrains.php.tools.quality.QualityToolValidationGlobalInspection;
import com.jetbrains.php.tools.quality.QualityToolXmlMessageProcessor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.intellij.openapi.util.text.StringUtil.isNotEmpty;
import static com.jetbrains.php.tools.quality.QualityToolAnnotator.updateIfRemote;

public class PhpStanGlobalInspection extends QualityToolValidationGlobalInspection {
  public static final Key<List<QualityToolXmlMessageProcessor.ProblemDescription>> PHPSTAN_ANNOTATOR_INFO = Key.create("ANNOTATOR_INFO_2");
  public boolean FULL_PROJECT = false;
  @NonNls public String memoryLimit = "2G";
  public int level = 4;
  public @NlsSafe String config = "";
  public @NlsSafe String autoload = "";

  @Override
  public JComponent createOptionsPanel() {
    final PhpStanOptionsPanel optionsPanel = new PhpStanOptionsPanel(this);
    optionsPanel.init();
    return optionsPanel.getOptionsPanel();
  }


  @Override
  public @Nullable LocalInspectionTool getSharedLocalInspectionTool() {
    return new PhpStanValidationInspection();
  }

  @Override
  protected @NotNull QualityToolAnnotator getAnnotator() {
    return PhpStanAnnotatorProxy.INSTANCE;
  }

  @Override
  protected Key<List<QualityToolXmlMessageProcessor.ProblemDescription>> getKey() {
    return PHPSTAN_ANNOTATOR_INFO;
  }

  public List<String> getCommandLineOptions(@NotNull List<String> filePath, @NotNull Project project) {
    @NonNls ArrayList<String> options = new ArrayList<>();
    options.add("analyze");
    if (isNotEmpty(config)) {
      options.add("-c");
      options.add(updateIfRemote(config, project, PhpStanQualityToolType.INSTANCE));
    }
    else {
      options.add("--level=" + level);
    }
    if (isNotEmpty(autoload)) {
      options.add("-a");
      options.add(updateIfRemote(autoload, project, PhpStanQualityToolType.INSTANCE));
    }
    options.add("--memory-limit=" + memoryLimit);
    options.add("--error-format=checkstyle");
    options.add("--no-progress");
    options.add("--no-ansi");
    options.add("--no-interaction");
    final List<String> filePaths = ContainerUtil.filter(filePath, Objects::nonNull);
    options.addAll(filePaths);
    return options;
  }
}

package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.php.tools.quality.ParsedProblem;
import com.jetbrains.php.tools.quality.QualityToolValidationGlobalInspection;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.intellij.openapi.util.text.StringUtil.isNotEmpty;
import static com.jetbrains.php.tools.quality.QualityToolAnnotator.updateIfRemoteMappingExists;

public class PhpStanGlobalInspection extends QualityToolValidationGlobalInspection {
  public boolean FULL_PROJECT = false;
  public @NonNls String memoryLimit = "2G";
  public int level = 4;
  public @NlsSafe String config = "";
  public @NlsSafe String autoload = "";
  public static final Key<List<ParsedProblem>> PHPSTAN_ANNOTATOR_INFO = Key.create("ANNOTATOR_INFO_2");

  @Override
  public @Nullable LocalInspectionTool getSharedLocalInspectionTool() {
    return new PhpStanValidationInspection();
  }

  @Override
  protected @NotNull PhpStanAnnotatorProxy getAnnotator() {
    return PhpStanAnnotatorProxy.INSTANCE;
  }

  @Override
  protected Key<List<ParsedProblem>> getKey() {
    return PHPSTAN_ANNOTATOR_INFO;
  }

  public List<String> getBaseCommandLineOptions(@NotNull List<String> filePath, @NotNull Project project) {
    return getBaseCommandLineOptions(filePath, project, null);
  }

  public List<String> getBaseCommandLineOptions(@NotNull List<String> filePath,
                                                 @NotNull Project project,
                                                 @Nullable String originalFilePath) {
    @NonNls ArrayList<String> options = new ArrayList<>();
    PhpStanOptionsConfiguration configuration = PhpStanOptionsConfiguration.getInstance(project);
    options.add("analyze");
    if (isNotEmpty(configuration.getConfig())) {
      options.add("-c");
      options.add(updateIfRemoteMappingExists(configuration.getConfig(), project, PhpStanQualityToolType.INSTANCE));
    }
    else {
      options.add("--level=" + configuration.getLevel());
    }
    if (isNotEmpty(configuration.getAutoload())) {
      options.add("-a");
      options.add(updateIfRemoteMappingExists(configuration.getAutoload(), project, PhpStanQualityToolType.INSTANCE));
    }
    options.add("--memory-limit=" + configuration.getMemoryLimit());
    List<String> filePaths = ContainerUtil.filter(filePath, Objects::nonNull);
    filePaths = ContainerUtil.map(filePaths, it -> updateIfRemoteMappingExists(it, project, PhpStanQualityToolType.INSTANCE));
    if (originalFilePath != null && filePaths.size() == 1) {
      String mappedOriginal = updateIfRemoteMappingExists(originalFilePath, project, PhpStanQualityToolType.INSTANCE);
      options.add("--tmp-file");
      options.add(filePaths.getFirst());
      options.add("--instead-of");
      options.add(mappedOriginal);
      options.add(mappedOriginal);
    }
    else {
      options.addAll(filePaths);
    }
    return options;
  }
}

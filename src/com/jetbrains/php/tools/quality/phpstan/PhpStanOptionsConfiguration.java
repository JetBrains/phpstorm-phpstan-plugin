package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.jetbrains.php.tools.quality.QualityToolsOptionsConfiguration;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "PhpStanOptionsConfiguration", storages = @Storage("php.xml"))
public class PhpStanOptionsConfiguration extends QualityToolsOptionsConfiguration implements PersistentStateComponent<PhpStanOptionsConfiguration> {
  private boolean fullProject = false;
  @NonNls private String memoryLimit = "2G";
  private int level = 4;
  @NlsSafe private String config = "";
  @NlsSafe private String autoload = "";

  public boolean isFullProject() {
    return fullProject;
  }

  public void setFullProject(boolean fullProject) {
    this.fullProject = fullProject;
  }

  public String getMemoryLimit() {
    return memoryLimit;
  }

  public void setMemoryLimit(String memoryLimit) {
    this.memoryLimit = memoryLimit;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public String getConfig() {
    return config;
  }

  public void setConfig(String config) {
    this.config = config;
  }

  public String getAutoload() {
    return autoload;
  }

  public void setAutoload(String autoload) {
    this.autoload = autoload;
  }

  @Nullable
  @Override
  public PhpStanOptionsConfiguration getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull PhpStanOptionsConfiguration state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public static PhpStanOptionsConfiguration getInstance(Project project) {
    return project.getService(PhpStanOptionsConfiguration.class);
  }
}

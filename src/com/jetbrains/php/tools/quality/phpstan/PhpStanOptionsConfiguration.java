package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Property;
import com.jetbrains.php.tools.quality.QualityToolRateLimitSettings;
import com.jetbrains.php.tools.quality.QualityToolsOptionsConfiguration;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "PhpStanOptionsConfiguration", storages = @Storage("php.xml"))
public class PhpStanOptionsConfiguration extends QualityToolsOptionsConfiguration
  implements PersistentStateComponent<PhpStanOptionsConfiguration> {
  private boolean fullProject = false;
  private @NonNls String memoryLimit = "2G";
  private int level = 4;
  private @NlsSafe String config = "";
  private @NlsSafe String autoload = "";
  private QualityToolRateLimitSettings rateLimitSettings = new QualityToolRateLimitSettings();

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

  @Property(flat = true)
  public @NotNull QualityToolRateLimitSettings getRateLimitSettings() {
    return rateLimitSettings;
  }

  @SuppressWarnings("UnusedDeclaration")
  public void setRateLimitSettings(@NotNull QualityToolRateLimitSettings rateLimitSettings) {
    this.rateLimitSettings = rateLimitSettings;
  }

  @Override
  public @Nullable PhpStanOptionsConfiguration getState() {
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

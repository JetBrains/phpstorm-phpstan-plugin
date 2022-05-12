package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.php.tools.quality.QualityToolConfiguration;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurationManager.DEFAULT_MAX_MESSAGES_PER_FILE;


/**
 * Stores configuration needed to run PHPStan in selected environment.
 */
public class PhpStanConfiguration implements QualityToolConfiguration {
  private String myPhpStanPath = "";
  private int myMaxMessagesPerFile = DEFAULT_MAX_MESSAGES_PER_FILE;
  private int myTimeoutMs = 30000;

  @Override
  @Transient
  public String getToolPath() {
    return myPhpStanPath;
  }

  @Override
  public void setToolPath(String toolPath) {
    myPhpStanPath = toolPath;
  }

  @SuppressWarnings("UnusedDeclaration")
  @Nullable
  @Attribute("tool_path")
  public String getSerializedToolPath() {
    return serialize(myPhpStanPath);
  }

  @SuppressWarnings("UnusedDeclaration")
  public void setSerializedToolPath(@Nullable String configurationFilePath) {
    myPhpStanPath = deserialize(configurationFilePath);
  }

  @Override
  @Attribute("max_messages_per_file")
  public int getMaxMessagesPerFile() {
    return myMaxMessagesPerFile;
  }

  @Override
  @Attribute("timeout")
  public int getTimeout() {
    return myTimeoutMs;
  }

  @Override
  public void setTimeout(int timeout) {
    myTimeoutMs = timeout;
  }

  @Override
  @NotNull
  public @Nls String getId() {
    return PhpStanBundle.message("local");
  }

  @Override
  @Nullable
  public String getInterpreterId() {
    return null;
  }

  @Override
  public PhpStanConfiguration clone() {
    PhpStanConfiguration settings = new PhpStanConfiguration();
    clone(settings);
    return settings;
  }

  public PhpStanConfiguration clone(@NotNull PhpStanConfiguration settings) {
    settings.myPhpStanPath = myPhpStanPath;
    settings.myMaxMessagesPerFile = myMaxMessagesPerFile;
    settings.myTimeoutMs = myTimeoutMs;
    return settings;
  }

  @Override
  public int compareTo(@NotNull QualityToolConfiguration o) {
    if (!(o instanceof PhpStanConfiguration)) {
      return 1;
    }

    if (StringUtil.equals(getPresentableName(null), PhpStanBundle.message("label.system.php"))) {
      return -1;
    }
    else if (StringUtil.equals(o.getPresentableName(null), PhpStanBundle.message("label.system.php"))) {
      return 1;
    }
    return StringUtil.compare(getPresentableName(null), o.getPresentableName(null), false);
  }
}

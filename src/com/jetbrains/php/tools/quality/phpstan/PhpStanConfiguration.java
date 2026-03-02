package com.jetbrains.php.tools.quality.phpstan;

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
public class PhpStanConfiguration extends QualityToolConfiguration {
  private String myPhpStanPath = "";
  private int myMaxMessagesPerFile = DEFAULT_MAX_MESSAGES_PER_FILE;
  private int myTimeoutMs = 30000;
  private @Nullable String myVersion = null;

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
  @Attribute("tool_path")
  public @Nullable String getSerializedToolPath() {
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

  /**
   * Gets the detected PHPStan version.
   * Used to determine if editor mode (--tmp-file/--instead-of) is supported.
   */
  @Transient
  public @Nullable String getVersion() {
    return myVersion;
  }

  /**
   * Sets the detected PHPStan version.
   * Called during tool validation when the version is extracted from PHPStan output.
   */
  public void setVersion(@Nullable String version) {
    myVersion = version;
  }

  @SuppressWarnings("UnusedDeclaration")
  @Attribute("version")
  public @Nullable String getSerializedVersion() {
    return myVersion;
  }

  @SuppressWarnings("UnusedDeclaration")
  public void setSerializedVersion(@Nullable String version) {
    myVersion = version;
  }

  @Override
  public @NotNull @Nls String getId() {
    return PhpStanBundle.message("local");
  }

  @Override
  public @Nullable String getInterpreterId() {
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
    settings.myVersion = myVersion;
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

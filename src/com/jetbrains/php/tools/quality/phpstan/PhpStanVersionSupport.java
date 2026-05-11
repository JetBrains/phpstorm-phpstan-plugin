package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.Version;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Utility class to check PHPStan version support for various features.
 * 
 * Editor mode (--tmp-file/--instead-of) is supported in:
 * - PHPStan 1.12.27 and greater
 * - PHPStan 2.1.17 and greater
 *
 * @see <a href="https://phpstan.org/user-guide/editor-mode">PHPStan Editor Mode Documentation</a>
 */
public final class PhpStanVersionSupport {
  
  private static final Logger LOG = Logger.getInstance(PhpStanVersionSupport.class);
  
  // Timeout for version detection process (seconds)
  private static final int VERSION_DETECTION_TIMEOUT_SECONDS = 5;
  
  // Minimum version for editor mode support in 1.x branch
  private static final Version MIN_1X_Version = new Version(1,12,27);
  // Minimum version for editor mode support in 2.x branch
  private static final Version MIN_2X_Version = new Version(2,1,17);

  // Regex pattern to extract version from PHPStan output like "PHPStan - PHP Static Analysis Tool 1.12.27"
  private static final String VERSION_EXTRACTION_PATTERN = "PHPStan.* ([\\d.]*).*";

  private PhpStanVersionSupport() {
    // Utility class, no instantiation
  }

  /**
   * Extracts the version string from PHPStan's --version output.
   * Parses output like "PHPStan - PHP Static Analysis Tool 1.12.27" to extract "1.12.27".
   *
   * @param output The output from running PHPStan (e.g., from --version command or validation)
   * @return The extracted version string, or null if extraction fails
   */
  public static @Nullable String extractVersionFromOutput(@Nullable String output) {
    if (output == null || output.isEmpty()) {
      return null;
    }
    String versionString = output.trim().replaceFirst(VERSION_EXTRACTION_PATTERN, "$1").trim();
    if (!versionString.isEmpty() && Character.isDigit(versionString.charAt(0))) {
      return versionString;
    }
    return null;
  }

  /**
   * Checks if the given PHPStan version supports editor mode.
   * Editor mode allows using --tmp-file and --instead-of CLI options
   * for proper ignoreErrors handling with temporary files.
   *
   * @param versionString The PHPStan version string (e.g., "1.12.27", "2.1.17")
   * @return true if the version supports editor mode, false otherwise
   */
  public static boolean supportsEditorMode(@Nullable String versionString) {
    if (versionString == null || versionString.isEmpty()) {
      return false;
    }
    
    Version version = extractVersion(versionString);
    if (version == null) {
      return false;
    }

    if (version.major >= 2) {
      return version.compareTo(MIN_2X_Version) >= 0;
    }

    return version.compareTo(MIN_1X_Version) >= 0;
  }

  /**
   * Detects the PHPStan version by running the tool with --version.
   * This method is used for automatic version detection when the version
   * is not already stored in the configuration (e.g., after plugin upgrade).
   *
   * @param toolPath The path to the PHPStan executable
   * @return The detected version string (e.g., "1.12.27"), or null if detection fails
   */
  public static @Nullable String detectVersion(@NotNull String toolPath) {
    LOG.info("Attempting to auto-detect PHPStan version from: " + toolPath);
    try {
      ProcessBuilder pb = new ProcessBuilder(toolPath, "--version");
      pb.redirectErrorStream(true);
      Process process = pb.start();
      
      // Wait for the process with a timeout
      boolean finished = process.waitFor(VERSION_DETECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
      if (!finished) {
        LOG.warn("PHPStan version detection timed out after " + VERSION_DETECTION_TIMEOUT_SECONDS + " seconds");
        process.destroyForcibly();
        return null;
      }
      
      String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
      int exitCode = process.exitValue();
      
      if (exitCode == 0) {
        String versionString = extractVersionFromOutput(output);
        if (versionString != null) {
          LOG.info("Auto-detected PHPStan version: " + versionString);
          return versionString;
        }
        LOG.warn("Could not parse PHPStan version from output: " + output);
      } else {
        LOG.warn("PHPStan --version exited with code " + exitCode + ": " + output);
      }
    } catch (Exception e) {
      LOG.warn("Failed to detect PHPStan version: " + e.getMessage(), e);
    }
    return null;
  }

  public static @Nullable Version extractVersion(@Nullable String versionString) {
        if (versionString == null || versionString.isEmpty()) {
            return null;
        }
        String[] chunks = versionString.split("\\.");
        int major = 0;
        int minor = 0;
        int nano = 0;

        try {
            if (chunks.length > 0) {
                major = Integer.parseInt(chunks[0]);
                if (chunks.length > 1) {
                    minor = Integer.parseInt(chunks[1]);
                    if (chunks.length > 2) {
                        nano = Integer.parseInt(chunks[2]);
                    }
                }
            }
        } catch (NumberFormatException var6) {
            return null;
        }

        return new Version(major, minor, nano);
    }

}

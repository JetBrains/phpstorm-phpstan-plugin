package com.jetbrains.php.phpstan.quality.tools;

import com.intellij.openapi.util.Version;
import com.jetbrains.php.tools.quality.phpstan.PhpStanVersionSupport;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for PHPStan version support detection.
 * Tests the version comparison logic for editor mode support.
 * 
 * Editor mode is supported in:
 * - PHPStan 1.12.27 and greater (1.x branch)
 * - PHPStan 2.1.17 and greater (2.x branch)
 * - Any future major version (3.x, 4.x, etc.) - assumed supported
 * 
 * @see <a href="https://phpstan.org/user-guide/editor-mode">PHPStan Editor Mode Documentation</a>
 */
public class PhpStanVersionSupportTest {

  // ==================== supportsEditorMode() Tests ====================

  // --- 1.x Branch: Minimum supported version is 1.12.27 ---
  
  @Test
  public void testSupportsEditorMode_1x_ExactMinimum() {
    assertTrue("1.12.27 (exact minimum) should support editor mode", 
        PhpStanVersionSupport.supportsEditorMode("1.12.27"));
  }

  @Test
  public void testSupportsEditorMode_1x_AboveMinimum() {
    assertTrue("1.12.28 should support editor mode", PhpStanVersionSupport.supportsEditorMode("1.12.28"));
    assertTrue("1.12.99 should support editor mode", PhpStanVersionSupport.supportsEditorMode("1.12.99"));
    assertTrue("1.13.0 should support editor mode", PhpStanVersionSupport.supportsEditorMode("1.13.0"));
    assertTrue("1.99.0 should support editor mode", PhpStanVersionSupport.supportsEditorMode("1.99.0"));
  }

  @Test
  public void testSupportsEditorMode_1x_BelowMinimum() {
    assertFalse("1.12.26 (one below minimum) should NOT support editor mode", 
        PhpStanVersionSupport.supportsEditorMode("1.12.26"));
    assertFalse("1.12.0 should NOT support editor mode", PhpStanVersionSupport.supportsEditorMode("1.12.0"));
    assertFalse("1.11.99 should NOT support editor mode", PhpStanVersionSupport.supportsEditorMode("1.11.99"));
    assertFalse("1.11.0 should NOT support editor mode", PhpStanVersionSupport.supportsEditorMode("1.11.0"));
    assertFalse("1.0.0 should NOT support editor mode", PhpStanVersionSupport.supportsEditorMode("1.0.0"));
  }

  // --- 2.x Branch: Minimum supported version is 2.1.17 ---
  
  @Test
  public void testSupportsEditorMode_2x_ExactMinimum() {
    assertTrue("2.1.17 (exact minimum) should support editor mode", 
        PhpStanVersionSupport.supportsEditorMode("2.1.17"));
  }

  @Test
  public void testSupportsEditorMode_2x_AboveMinimum() {
    assertTrue("2.1.18 should support editor mode", PhpStanVersionSupport.supportsEditorMode("2.1.18"));
    assertTrue("2.1.99 should support editor mode", PhpStanVersionSupport.supportsEditorMode("2.1.99"));
    assertTrue("2.2.0 should support editor mode", PhpStanVersionSupport.supportsEditorMode("2.2.0"));
    assertTrue("2.5.0 should support editor mode", PhpStanVersionSupport.supportsEditorMode("2.5.0"));
    assertTrue("2.99.99 should support editor mode", PhpStanVersionSupport.supportsEditorMode("2.99.99"));
  }

  @Test
  public void testSupportsEditorMode_2x_BelowMinimum() {
    assertFalse("2.1.16 (one below minimum) should NOT support editor mode", 
        PhpStanVersionSupport.supportsEditorMode("2.1.16"));
    assertFalse("2.1.0 should NOT support editor mode", PhpStanVersionSupport.supportsEditorMode("2.1.0"));
    assertFalse("2.0.99 should NOT support editor mode", PhpStanVersionSupport.supportsEditorMode("2.0.99"));
    assertFalse("2.0.0 should NOT support editor mode", PhpStanVersionSupport.supportsEditorMode("2.0.0"));
  }

  // --- Future Versions (3.x+): Assumed to support editor mode ---
  
  @Test
  public void testSupportsEditorMode_FutureVersions() {
    assertTrue("3.0.0 should support editor mode (future version)", 
        PhpStanVersionSupport.supportsEditorMode("3.0.0"));
    assertTrue("3.5.10 should support editor mode (future version)", 
        PhpStanVersionSupport.supportsEditorMode("3.5.10"));
    assertTrue("4.0.0 should support editor mode (future version)", 
        PhpStanVersionSupport.supportsEditorMode("4.0.0"));
    assertTrue("10.0.0 should support editor mode (future version)", 
        PhpStanVersionSupport.supportsEditorMode("10.0.0"));
  }

  // --- 0.x Versions: Never supported ---
  
  @Test
  public void testSupportsEditorMode_0x_NotSupported() {
    assertFalse("0.12.25 should NOT support editor mode", PhpStanVersionSupport.supportsEditorMode("0.12.25"));
    assertFalse("0.12.99 should NOT support editor mode", PhpStanVersionSupport.supportsEditorMode("0.12.99"));
    assertFalse("0.99.99 should NOT support editor mode", PhpStanVersionSupport.supportsEditorMode("0.99.99"));
    assertFalse("0.0.1 should NOT support editor mode", PhpStanVersionSupport.supportsEditorMode("0.0.1"));
  }

  // --- Edge Cases ---
  
  @Test
  public void testSupportsEditorMode_NullAndEmpty() {
    assertFalse("null should NOT support editor mode", PhpStanVersionSupport.supportsEditorMode(null));
    assertFalse("empty string should NOT support editor mode", PhpStanVersionSupport.supportsEditorMode(""));
    assertFalse("whitespace should NOT support editor mode", PhpStanVersionSupport.supportsEditorMode("   "));
  }

  @Test
  public void testSupportsEditorMode_InvalidVersions() {
    assertFalse("'abc' should NOT support editor mode", PhpStanVersionSupport.supportsEditorMode("abc"));
    assertFalse("'1.x' should NOT support editor mode", PhpStanVersionSupport.supportsEditorMode("1.x"));
    assertFalse("'version1.0' should NOT support editor mode", PhpStanVersionSupport.supportsEditorMode("version1.0"));
    assertFalse("'v1.12.27' should NOT support editor mode", PhpStanVersionSupport.supportsEditorMode("v1.12.27"));
  }

  @Test
  public void testSupportsEditorMode_PartialVersions() {
    // Versions with only major.minor (no patch) - patch defaults to 0
    assertFalse("1.12 should NOT support editor mode (patch defaults to 0)", 
        PhpStanVersionSupport.supportsEditorMode("1.12"));
    assertTrue("2.2 should support editor mode (2.2.0 >= 2.1.17)", 
        PhpStanVersionSupport.supportsEditorMode("2.2"));
    assertFalse("2.1 should NOT support editor mode (2.1.0 < 2.1.17)", 
        PhpStanVersionSupport.supportsEditorMode("2.1"));
  }

  // ==================== extractVersionFromOutput() Tests ====================

  @Test
  public void testExtractVersionFromOutput_StandardFormat() {
    assertEquals("1.12.27", 
        PhpStanVersionSupport.extractVersionFromOutput("PHPStan - PHP Static Analysis Tool 1.12.27"));
    assertEquals("2.1.17", 
        PhpStanVersionSupport.extractVersionFromOutput("PHPStan - PHP Static Analysis Tool 2.1.17"));
    assertEquals("0.12.25", 
        PhpStanVersionSupport.extractVersionFromOutput("PHPStan - PHP Static Analysis Tool 0.12.25"));
  }

  @Test
  public void testExtractVersionFromOutput_WithNewlines() {
    assertEquals("1.12.27", 
        PhpStanVersionSupport.extractVersionFromOutput("PHPStan - PHP Static Analysis Tool 1.12.27\n"));
    assertEquals("2.1.17", 
        PhpStanVersionSupport.extractVersionFromOutput("\nPHPStan - PHP Static Analysis Tool 2.1.17\n"));
  }

  @Test
  public void testExtractVersionFromOutput_DevVersion() {
    // Dev versions like "0.12.x-dev@41b16d5"
    String extracted = PhpStanVersionSupport.extractVersionFromOutput(
        "PHPStan - PHP Static Analysis Tool 0.12.x-dev@41b16d5");
    assertNotNull("Should extract something from dev version", extracted);
    assertTrue("Should start with 0.12", extracted.startsWith("0.12"));
  }

  @Test
  public void testExtractVersionFromOutput_InvalidOutput() {
    assertNull("Random text should return null", 
        PhpStanVersionSupport.extractVersionFromOutput("Some random text"));
    assertNull("Null input should return null", 
        PhpStanVersionSupport.extractVersionFromOutput(null));
    assertNull("Empty input should return null", 
        PhpStanVersionSupport.extractVersionFromOutput(""));
    assertNull("No version in output should return null", 
        PhpStanVersionSupport.extractVersionFromOutput("PHPStan - PHP Static Analysis Tool"));
  }

  // ==================== extractVersion() Tests ====================

  @Test
  public void testExtractVersion_ValidVersions() {
    Version v1 = PhpStanVersionSupport.extractVersion("1.12.27");
    assertNotNull(v1);
    assertEquals(1, v1.major);
    assertEquals(12, v1.minor);
    assertEquals(27, v1.bugfix);

    Version v2 = PhpStanVersionSupport.extractVersion("2.1.17");
    assertNotNull(v2);
    assertEquals(2, v2.major);
    assertEquals(1, v2.minor);
    assertEquals(17, v2.bugfix);

    Version v3 = PhpStanVersionSupport.extractVersion("0.12.25");
    assertNotNull(v3);
    assertEquals(0, v3.major);
    assertEquals(12, v3.minor);
    assertEquals(25, v3.bugfix);
  }

  @Test
  public void testExtractVersion_PartialVersions() {
    // Major.minor only - patch should default to 0
    Version v1 = PhpStanVersionSupport.extractVersion("1.12");
    assertNotNull(v1);
    assertEquals(1, v1.major);
    assertEquals(12, v1.minor);
    assertEquals(0, v1.bugfix);

    // Major only - minor and patch should default to 0
    Version v2 = PhpStanVersionSupport.extractVersion("2");
    assertNotNull(v2);
    assertEquals(2, v2.major);
    assertEquals(0, v2.minor);
    assertEquals(0, v2.bugfix);
  }

  @Test
  public void testExtractVersion_InvalidVersions() {
    assertNull("Null should return null", PhpStanVersionSupport.extractVersion(null));
    assertNull("Empty string should return null", PhpStanVersionSupport.extractVersion(""));
    assertNull("Non-numeric should return null", PhpStanVersionSupport.extractVersion("abc"));
    assertNull("Mixed invalid should return null", PhpStanVersionSupport.extractVersion("1.x.0"));
  }

  @Test
  public void testExtractVersion_LargeVersionNumbers() {
    Version v = PhpStanVersionSupport.extractVersion("99.999.9999");
    assertNotNull(v);
    assertEquals(99, v.major);
    assertEquals(999, v.minor);
    assertEquals(9999, v.bugfix);
  }

  // ==================== Integration Tests (End-to-End Scenarios) ====================

  @Test
  public void testEndToEnd_ExtractAndCheckVersion_Supported() {
    // Simulate the full flow: extract version from output, then check if supported
    String output = "PHPStan - PHP Static Analysis Tool 1.12.27";
    String version = PhpStanVersionSupport.extractVersionFromOutput(output);
    assertNotNull("Should extract version", version);
    assertTrue("Extracted version should support editor mode", 
        PhpStanVersionSupport.supportsEditorMode(version));
  }

  @Test
  public void testEndToEnd_ExtractAndCheckVersion_NotSupported() {
    // Old version that doesn't support editor mode
    String output = "PHPStan - PHP Static Analysis Tool 1.12.0";
    String version = PhpStanVersionSupport.extractVersionFromOutput(output);
    assertNotNull("Should extract version", version);
    assertFalse("Extracted version should NOT support editor mode", 
        PhpStanVersionSupport.supportsEditorMode(version));
  }

  @Test
  public void testEndToEnd_2xBranch_Supported() {
    String output = "PHPStan - PHP Static Analysis Tool 2.1.17";
    String version = PhpStanVersionSupport.extractVersionFromOutput(output);
    assertNotNull("Should extract version", version);
    assertTrue("PHPStan 2.1.17 should support editor mode", 
        PhpStanVersionSupport.supportsEditorMode(version));
  }

  @Test
  public void testEndToEnd_2xBranch_NotSupported() {
    String output = "PHPStan - PHP Static Analysis Tool 2.0.0";
    String version = PhpStanVersionSupport.extractVersionFromOutput(output);
    assertNotNull("Should extract version", version);
    assertFalse("PHPStan 2.0.0 should NOT support editor mode", 
        PhpStanVersionSupport.supportsEditorMode(version));
  }
}

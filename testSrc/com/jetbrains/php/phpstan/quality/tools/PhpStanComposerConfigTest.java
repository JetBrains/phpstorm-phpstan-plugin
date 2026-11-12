package com.jetbrains.php.phpstan.quality.tools;

import com.intellij.testFramework.junit5.TestApplication;
import com.intellij.testFramework.junit5.fixture.TestFixture;
import com.jetbrains.php.config.composer.QualityToolsComposerConfigTestFixture;
import com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurationManager;
import org.junit.jupiter.api.Test;

import static com.jetbrains.php.config.composer.QualityToolsComposerConfigTestFixture.qualityToolsComposerConfigFixture;

@TestApplication
public class PhpStanComposerConfigTest {
  private final TestFixture<QualityToolsComposerConfigTestFixture> qualityTool =
    qualityToolsComposerConfigFixture(project -> PhpStanConfigurationManager.getInstance(project), "phpstan", "phpstan/phpstan");

  @Test
  public void testGeneral() {
    qualityTool.get().doTestGeneral();
  }

  @Test
  public void testNoVersion() {
    qualityTool.get().doTestNoVersion();
  }

  @Test
  public void testEmpty() {
    qualityTool.get().doTestEmpty();
  }

  @Test
  public void testEmptyJSON() {
    qualityTool.get().doTestEmptyJSON();
  }
}

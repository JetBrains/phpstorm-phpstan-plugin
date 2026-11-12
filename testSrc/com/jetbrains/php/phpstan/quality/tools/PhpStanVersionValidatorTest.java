package com.jetbrains.php.phpstan.quality.tools;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.testFramework.junit5.TestApplication;
import com.intellij.testFramework.junit5.fixture.TestFixture;
import com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurableForm;
import com.jetbrains.php.tools.quality.phpstan.PhpStanConfiguration;
import org.junit.jupiter.api.Test;

import static com.intellij.platform.testFramework.junit5.codeInsight.fixture.CodeInsightFixtureKt.codeInsightProjectFixture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestApplication
public class PhpStanVersionValidatorTest {
  private final TestFixture<Project> projectFixture = codeInsightProjectFixture();

  @Test
  public void testSimple(){
    final String STRING = "PHPStan - PHP Static Analysis Tool 0.12.25";
    final Pair<Boolean, String> result = new PhpStanConfigurableForm<>(projectFixture.get(), new PhpStanConfiguration())
      .validateMessage(STRING);
    assertTrue(result.first);
    assertEquals("OK, " + STRING, result.second);
  }

  @Test
  public void testDevMaster(){
    final String message = "PHPStan - PHP Static Analysis Tool 0.12.x-dev@41b16d5";
    final Pair<Boolean, String> result = new PhpStanConfigurableForm<>(projectFixture.get(), new PhpStanConfiguration())
      .validateMessage(message);
    assertTrue(result.first);
    assertEquals("OK, " + message, result.second);
  }
}

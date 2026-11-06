package com.jetbrains.php.phpstan.quality.tools;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.junit5.TestApplication;
import com.intellij.testFramework.junit5.fixture.TestFixture;
import com.jetbrains.php.tools.quality.phpstan.PhpStanComposerConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import static com.intellij.platform.testFramework.junit5.codeInsight.fixture.CodeInsightFixtureKt.codeInsightFixture;
import static com.intellij.platform.testFramework.junit5.codeInsight.fixture.CodeInsightFixtureKt.codeInsightProjectFixture;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestApplication
public class PhpStanConfigFileFromComposerTest {
  private final TestFixture<Project> projectFixture = codeInsightProjectFixture();
  private final TestFixture<CodeInsightTestFixture> codeInsightFixture = codeInsightFixture(projectFixture);

  private void doRulesetTest(@NotNull String content, @Nullable String expectedPath) {
    CodeInsightTestFixture myFixture = codeInsightFixture.get();
    myFixture.configureByText("composer.json", content);
    VirtualFile configFile = myFixture.getFile().getVirtualFile();
    assertEquals(expectedPath, new PhpStanComposerConfig().getRuleset(configFile));
  }

  @Test
  public void testCFormat() {
    doRulesetTest("""
                    
                    {
                        "scripts": {
                            "phpstan": "vendor/bin/phpstan analyse -l 2 -c tests/phpstan/phpstan.neon system/src --memory-limit=256M"
                        }
                    }
                    """, "tests/phpstan/phpstan.neon");
  }

  @Test
  public void testNoScriptsSection() {
    doRulesetTest("""
                    
                    {
                    }
                    """, null);
  }

  @Test
  public void testNoPHPStanInScriptsSection() {
    doRulesetTest("""
                    
                    {
                        "scripts": {
                        }
                    }
                    """, null);
  }

  @Test
  public void testConfigFormat() {
    doRulesetTest("""
                    
                    {
                      "scripts": {
                            "phpstan": "vendor/bin/phpstan analyse -l 2 --configuration=tests/phpstan/phpstan.neon system/src --memory-limit=256M"
                      }
                    }
                    """, "tests/phpstan/phpstan.neon");
  }

  @Test
  public void testNoConfigArg() {
    doRulesetTest("""
                    
                    {
                      "scripts": {
                            "phpstan": "vendor/bin/phpstan analyse -l 2 system/src --memory-limit=256M"
                      }
                    }
                    """, null);
  }

  @Test
  public void testWrongFormat() {
    doRulesetTest("""
                    
                    {
                    "scripts": {
                            "phpstan": "vendor/bin/phpstan analyse -l 2 -c"
                      }
                    }
                    """, null);
  }
}

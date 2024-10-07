package com.jetbrains.php.phpstan.quality.tools;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase;
import com.jetbrains.php.tools.quality.phpstan.PhpStanComposerConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhpStanConfigFileFromComposerTest extends CodeInsightFixtureTestCase {
  private void doRulesetTest(@NotNull String content, @Nullable String expectedPath) {
    myFixture.configureByText("composer.json", content);
    VirtualFile configFile = myFixture.getFile().getVirtualFile();
    assertEquals(expectedPath, new PhpStanComposerConfig().getRuleset(configFile));
  }

  public void testCFormat() {
    doRulesetTest("""
                    
                    {
                        "scripts": {
                            "phpstan": "vendor/bin/phpstan analyse -l 2 -c tests/phpstan/phpstan.neon system/src --memory-limit=256M"
                        }
                    }
                    """, "tests/phpstan/phpstan.neon");
  }

  public void testNoScriptsSection() {
    doRulesetTest("""
                    
                    {
                    }
                    """, null);
  }

  public void testNoPHPStanInScriptsSection() {
    doRulesetTest("""
                    
                    {
                        "scripts": {
                        }
                    }
                    """, null);
  }

  public void testConfigFormat() {
    doRulesetTest("""
                    
                    {
                      "scripts": {
                            "phpstan": "vendor/bin/phpstan analyse -l 2 --configuration=tests/phpstan/phpstan.neon system/src --memory-limit=256M"
                      }
                    }
                    """, "tests/phpstan/phpstan.neon");
  }

  public void testNoConfigArg() {
    doRulesetTest("""
                    
                    {
                      "scripts": {
                            "phpstan": "vendor/bin/phpstan analyse -l 2 system/src --memory-limit=256M"
                      }
                    }
                    """, null);
  }

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

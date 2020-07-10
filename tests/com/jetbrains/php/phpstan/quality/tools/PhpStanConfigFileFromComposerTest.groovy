package com.jetbrains.php.phpstan.quality.tools

import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase
import com.jetbrains.php.tools.quality.phpstan.PhpStanComposerConfig
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

class PhpStanConfigFileFromComposerTest extends CodeInsightFixtureTestCase {

  @NotNull
  private void doRulesetTest(@NotNull String content, @Nullable String expectedPath) {
    myFixture.configureByText("composer.json", content)
    try {
      def configFile = myFixture.getFile().getVirtualFile()
      assertEquals(expectedPath, new PhpStanComposerConfig().getRuleset(configFile))
    }
    finally {
    }
  }

  void testCFormat() {
    doRulesetTest('''
{
    "scripts": {
        "phpstan": "vendor/bin/phpstan analyse -l 2 -c tests/phpstan/phpstan.neon system/src --memory-limit=256M"
    }
}
''', "tests/phpstan/phpstan.neon")
  }

  void testNoScriptsSection() {
    doRulesetTest('''
{
}
''', null)
  }

  void testNoPsalmInScriptsSection() {
    doRulesetTest('''
{
    "scripts": {
    }
}
''', null)
  }
  

  void testConfigFormat() {
    doRulesetTest('''
{
  "scripts": {
        "phpstan": "vendor/bin/phpstan analyse -l 2 --configuration=tests/phpstan/phpstan.neon system/src --memory-limit=256M"
  }
}
''', "tests/phpstan/phpstan.neon")
  }

  void testNoConfigArg() {
    doRulesetTest('''
{
  "scripts": {
        "phpstan": "vendor/bin/phpstan analyse -l 2 system/src --memory-limit=256M"
  }
}
''', null)
  }


  void testWrongFormat() {
    doRulesetTest('''
{
"scripts": {
        "phpstan": "vendor/bin/phpstan analyse -l 2 -c"
  }
}
''', null)
  }

}

package com.jetbrains.php.phpstan.completion;

import com.intellij.openapi.project.Project;
import com.intellij.testFramework.TestIndexingModeSupporter.IndexingMode;
import com.intellij.testFramework.junit5.TestApplication;
import com.intellij.testFramework.junit5.fixture.TestFixture;
import com.jetbrains.php.fixtures.PhpCompletionTestFixture;
import com.jetbrains.php.fixtures.PhpIndexingModeTest;
import com.jetbrains.php.fixtures.PhpTestDataLayout;

import static com.jetbrains.php.fixtures.PhpCompletionTestFixture.phpCompletionFixture;
import static com.jetbrains.php.fixtures.PhpTestFixtures.phpProjectFixture;

@TestApplication
public class PhpStanCompletionTest {
  private static final TestFixture<Project> projectFixture = phpProjectFixture();
  private final TestFixture<PhpCompletionTestFixture> php =
    phpCompletionFixture(projectFixture, new PhpTestDataLayout("completion").withHome("/phpstorm/phpstan/testData/"));

  @PhpIndexingModeTest
  public void testDocTagCompletions(IndexingMode mode) {
    php.get().runInMode(mode, () -> {
      php.get().doTestLookupElementsContains("phpstan-require-extends", "phpstan-require-implements");
    });
  }
}

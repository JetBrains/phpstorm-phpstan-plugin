import com.intellij.openapi.util.Pair;
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase;
import com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurableForm;
import com.jetbrains.php.tools.quality.phpstan.PhpStanConfiguration;

public class PhpStanVersionValidatorTest extends CodeInsightFixtureTestCase {

  public void testSimple(){
    final String STRING = "PHPStan - PHP Static Analysis Tool 0.12.25";
    final Pair<Boolean, String> result = new PhpStanConfigurableForm<>(myFixture.getProject(), new PhpStanConfiguration())
      .validateMessage(STRING);
    assertTrue(result.first);
    assertEquals("OK, " + STRING, result.second);
  }

  public void testDevMaster(){
    final String message = "PHPStan - PHP Static Analysis Tool 0.12.x-dev@41b16d5";
    final Pair<Boolean, String> result = new PhpStanConfigurableForm<>(myFixture.getProject(), new PhpStanConfiguration())
      .validateMessage(message);
    assertTrue(result.first);
    assertEquals("OK, " + message, result.second);
  }
}

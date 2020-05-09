import com.jetbrains.php.fixtures.PhpHeavyCodeInsightFixtureTestCase;
import com.jetbrains.php.tools.quality.phpstan.PhpStanConfigurationManager;
import com.jetbrains.php.tools.quality.phpstan.PhpStanValidationInspection;
import org.jetbrains.annotations.NotNull;

public class PhpStanOutputParsingTest extends PhpHeavyCodeInsightFixtureTestCase {
 
  public void testSimple() {
    PhpStanConfigurationManager.getInstance(myFixture.getProject()).getLocalSettings().setToolPath("phpstan"); // Dummy, needed to run annotator
    configureByFiles(getFileBeforeRelativePath().replace(".php", ".txt"));
    myFixture.enableInspections(PhpStanValidationInspection.class);
    myFixture.testHighlighting(true, false, true);
  }

  @Override
  protected String getFixtureTestDataFolder() {
    return "output";
  }

  @NotNull
  @Override
  protected String getFileBeforeExtension() {
    return "php";
  }

  @Override
  protected String getBasePath() {
    return "/phpstorm/phpstan/testData/output";
  }
}

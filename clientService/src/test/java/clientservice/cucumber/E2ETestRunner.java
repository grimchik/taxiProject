package clientservice.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/e2e/e2etest.feature",
        glue = "clientservice.cucumber.e2e",
        plugin = {"pretty", "html:target/cucumber-report.html"}
)
public class E2ETestRunner {
}

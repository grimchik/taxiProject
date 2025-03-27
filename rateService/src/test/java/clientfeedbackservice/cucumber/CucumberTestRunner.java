package clientfeedbackservice.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/clientfeedbackcontroller.feature",
        glue = "clientfeedbackservice.cucumber.steps",
        plugin = {"pretty", "html:target/cucumber-report.html"}
)
public class CucumberTestRunner {
}
package pe.interbank.bfa.front.runner;

import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import net.serenitybdd.cucumber.CucumberWithSerenity;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        plugin = {"pretty"},
        features = "src/test/resources/features",
        glue = "pe.interbank.bfa.front.stepdefinitions",
        tags ="@portableAuthentication"
)
public class PortableRunner {
}

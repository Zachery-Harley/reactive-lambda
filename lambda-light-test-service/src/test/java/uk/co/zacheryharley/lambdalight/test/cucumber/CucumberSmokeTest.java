package uk.co.zacheryharley.lambdalight.test.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("uk/co/zacheryharley/lambdalight/test/cucumber")
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "uk.co.zacheryharley.lambdalight.test.cucumber"
)
public class CucumberSmokeTest {
}

package uk.co.zacheryharley.lambdalight.test.cucumber.steps;

import io.cucumber.java8.En;
import uk.co.zacheryharley.lambdalight.test.TestContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ResponseStepDefs implements En {

    public ResponseStepDefs() {
        Then("the response payload will be {string}", (String payload) -> {
            assertThat(TestContext.getActiveResponse().getMessage(), is(payload));
        });
    }
}

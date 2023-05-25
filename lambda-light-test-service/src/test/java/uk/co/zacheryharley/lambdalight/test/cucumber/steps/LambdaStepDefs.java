package uk.co.zacheryharley.lambdalight.test.cucumber.steps;

import io.cucumber.java8.En;
import uk.co.zacheryharley.lambdalight.test.LambdaMessageBuilder;
import uk.co.zacheryharley.lambdalight.test.ServiceResponse.Source;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.co.zacheryharley.lambdalight.test.TestContext.*;
import static uk.co.zacheryharley.lambdalight.test.resource.AwsResource.getLambda;

public class LambdaStepDefs implements En {
    public LambdaStepDefs() {
        When("the SQS message {string} is sent to the lambda {string}", (String message, String functionName) -> {
            executeAction(getLambda(functionName).actions().invokeAndWait(
                LambdaMessageBuilder.getSqsReceiveMessageWithBody(message)
            ));
        });


        Then("the lambda {string} will respond successfully", (String functionName) -> {
            setActiveResponseAsMostRecent(Source.LAMBDA, functionName);
            assertThat(getActiveResponse().isSuccess(), is(true));
        });
    }
}

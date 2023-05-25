package uk.co.zacheryharley.lambdalight.test.cucumber.steps;

import io.cucumber.java8.En;

import static uk.co.zacheryharley.lambdalight.test.TestContext.executeAction;
import static uk.co.zacheryharley.lambdalight.test.TestContext.executeRevertibleAction;
import static uk.co.zacheryharley.lambdalight.test.resource.AwsResource.getLambda;

public class MyStepdefs implements En {

    public MyStepdefs() {
        Given("the lambda {string} triggers are all disabled", (String functionName) -> {
            executeRevertibleAction(getLambda(functionName).actions().disableAllTriggers());
        });



        Then("the lambda {string} respond with {string}", (String functionName, String response) -> {
            System.out.println(functionName);
            System.out.println(response);
        });
    }
}

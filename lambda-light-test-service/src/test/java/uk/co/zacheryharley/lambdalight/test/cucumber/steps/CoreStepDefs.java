package uk.co.zacheryharley.lambdalight.test.cucumber.steps;

import io.cucumber.java8.En;
import uk.co.zacheryharley.lambdalight.test.TestContext;

public class CoreStepDefs implements En {
    public CoreStepDefs() {
        After(hook -> {
            TestContext.revertAllActions();
            TestContext.reset();
        });
    }
}

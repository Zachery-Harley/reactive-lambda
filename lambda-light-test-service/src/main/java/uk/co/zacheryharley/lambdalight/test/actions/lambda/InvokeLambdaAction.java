package uk.co.zacheryharley.lambdalight.test.actions.lambda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import uk.co.zacheryharley.lambdalight.test.Action;
import uk.co.zacheryharley.lambdalight.test.ServiceResponse;
import uk.co.zacheryharley.lambdalight.test.TestContext;
import uk.co.zacheryharley.lambdalight.test.resource.Lambda;

public class InvokeLambdaAction implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvokeLambdaAction.class);
    private final Lambda lambda;
    private final String message;
    private final boolean awaitResponse;

    public InvokeLambdaAction(Lambda lambda, String message, boolean awaitResponse) {
        this.lambda = lambda;
        this.message = message;
        this.awaitResponse = awaitResponse;
    }

    @Override
    public boolean fire() {
        LOGGER.info("Triggering Action: Invoke Lambda: [{}]. Awaiting Response: [{}]", lambda.getFunctionName(), awaitResponse);
        InvokeResponse response = lambda.invoke(message, awaitResponse);

        if (awaitResponse) {
            TestContext.registerResponse(ServiceResponse.lambda()
                .withMessage(response.payload().asUtf8String())
                .isSuccess(response.functionError() == null)
                .withResourceName(lambda.getFunctionName())
                .withStatusCode(response.statusCode()));
        }

        return response.functionError() == null;
    }

    @Override
    public boolean revert() {
        return true;
    }

}

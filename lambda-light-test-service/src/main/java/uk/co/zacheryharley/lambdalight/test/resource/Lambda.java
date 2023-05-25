package uk.co.zacheryharley.lambdalight.test.resource;

import io.cucumber.java.en_old.Ac;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.model.EventSourceMappingConfiguration;
import software.amazon.awssdk.services.lambda.model.GetFunctionResponse;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import uk.co.zacheryharley.lambdalight.test.Action;
import uk.co.zacheryharley.lambdalight.test.actions.lambda.DisableAllTriggersAction;
import uk.co.zacheryharley.lambdalight.test.actions.lambda.InvokeLambdaAction;

import java.util.ArrayList;
import java.util.List;

import static uk.co.zacheryharley.lambdalight.test.config.LambdaConfiguration.getLambdaClient;

public class Lambda {
    private final String functionName;
    private GetFunctionResponse function;
    private List<EventSourceMappingConfiguration> eventSourceMappings;

    public Lambda(String name) {
        this.functionName = name;
        refreshEventSourceMappings();
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<EventSourceMappingConfiguration> getEventSourceMappings() {
        return new ArrayList<>(this.eventSourceMappings);
    }

    public InvokeResponse invoke(String jsonBody, boolean awaitResponse) {
        return getLambdaClient().invoke(request -> request.functionName(functionName)
            .invocationType(awaitResponse ? InvocationType.REQUEST_RESPONSE : InvocationType.EVENT)
            .payload(SdkBytes.fromUtf8String(jsonBody)));
    }

    private void refreshFunction() {
        this.function = getLambdaClient().getFunction(request -> request.functionName(functionName));
    }

    private void refreshEventSourceMappings() {
        eventSourceMappings = getLambdaClient().listEventSourceMappings(request -> request.functionName(functionName))
            .eventSourceMappings();
    }

    public LambdaActions actions() {
        return new LambdaActions(this);
    }

    public static class LambdaActions {
        private final Lambda lambda;

        public LambdaActions(Lambda lambda) {
            this.lambda = lambda;
        }

        public Action disableAllTriggers() {
            return new DisableAllTriggersAction(lambda);
        }

        public Action invoke(String message) {
            return new InvokeLambdaAction(lambda, message, false);
        }

        public Action invokeAndWait(String message) {
            return new InvokeLambdaAction(lambda, message, true);
        }

    }


}

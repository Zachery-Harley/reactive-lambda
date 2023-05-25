package uk.co.zacheryharley.lambdalight.test;

import software.amazon.awssdk.services.lambda.model.GetEventSourceMappingResponse;

import java.time.Duration;

import static uk.co.zacheryharley.lambdalight.test.config.LambdaConfiguration.getLambdaClient;

public class EventSourceMappingUtils {
    public enum State {
        CREATING(false, false),
        ENABLING(true, true),
        ENABLED(true, true),
        DISABLING(true, false),
        DISABLED(true, false),
        UPDATING(false, false),
        DELETING(false, false);

        private final boolean valid;
        private final boolean enabled;

        State(boolean valid, boolean enabled) {
            this.valid = valid;
            this.enabled = enabled;
        }

        public boolean isValid() {
            return valid;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }

    public static GetEventSourceMappingResponse getEventSourceMapping(String uuid) {
        return getLambdaClient().getEventSourceMapping(request -> request.uuid(uuid));
    }

    public static State getState(String uuid) {
        return State.valueOf(getEventSourceMapping(uuid).state().toUpperCase());
    }

    public static void updateState(String uuid, State state) {
        getLambdaClient().updateEventSourceMapping(request -> request.enabled(state.isEnabled()).uuid(uuid));
    }

    public static boolean awaitState(String uuid, State targetState) {
        try {
            RetryUtil.retryUntil(() -> getState(uuid), state -> state == targetState, Duration.ofMinutes(1));
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}

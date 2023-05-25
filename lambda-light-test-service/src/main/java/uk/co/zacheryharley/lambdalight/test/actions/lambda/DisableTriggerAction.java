package uk.co.zacheryharley.lambdalight.test.actions.lambda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.zacheryharley.lambdalight.test.Action;
import uk.co.zacheryharley.lambdalight.test.EventSourceMappingUtils;
import uk.co.zacheryharley.lambdalight.test.EventSourceMappingUtils.State;

public class DisableTriggerAction implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(DisableTriggerAction.class);
    private final String uuid;
    private State originalState;

    public DisableTriggerAction(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean fire() {
        this.originalState = EventSourceMappingUtils.getState(uuid);
        LOGGER.info("Triggering Action: DisableTrigger UUID: [{}]. Original State: [{}]", uuid, originalState);

        if (this.originalState.isEnabled()) {
            EventSourceMappingUtils.updateState(uuid, State.DISABLED);
            EventSourceMappingUtils.awaitState(uuid, State.DISABLED);
        }
        return true;
    }

    @Override
    public boolean revert() {
        LOGGER.info("Reverting Action: DisableTrigger UUID: [{}]. Original State: [{}]", uuid, originalState);

        if (this.originalState.isEnabled()) {
            EventSourceMappingUtils.updateState(uuid, State.ENABLED);
            EventSourceMappingUtils.awaitState(uuid, State.ENABLED);
        }

        return true;
    }

}

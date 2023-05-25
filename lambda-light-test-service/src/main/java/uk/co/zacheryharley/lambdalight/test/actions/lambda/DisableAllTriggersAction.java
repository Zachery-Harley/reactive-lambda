package uk.co.zacheryharley.lambdalight.test.actions.lambda;

import uk.co.zacheryharley.lambdalight.test.Action;
import uk.co.zacheryharley.lambdalight.test.resource.Lambda;

import java.util.List;
import java.util.stream.Collectors;

public class DisableAllTriggersAction implements Action {
    private final Lambda lambda;
    private List<Action> actions;

    public DisableAllTriggersAction(Lambda lambda) {
        this.lambda = lambda;
    }

    @Override
    public boolean fire() {
        this.actions = lambda.getEventSourceMappings().stream()
            .map(mapping -> new DisableTriggerAction(mapping.uuid()))
            .collect(Collectors.toList());

        this.actions.forEach(Action::fire);
        return true;
    }

    @Override
    public boolean revert() {
        this.actions.forEach((Action::revert));
        return true;
    }
}

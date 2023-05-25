package uk.co.zacheryharley.lambdalight.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class TestContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestContext.class);
    private static List<Action> revertibleActions = new ArrayList<>();
    private static List<Action> actions = new ArrayList<>();

    private static List<ServiceResponse> responses = new ArrayList<>();
    private static ServiceResponse activeResponse;

    //Map to store key value pairs on active elements.
    private static Map<String, Object> activeResources = new ConcurrentHashMap<>();

    public static void reset() {
        LOGGER.info("Test Context resetting");
        revertibleActions = new ArrayList<>();
        actions = new ArrayList<>();

        responses = new ArrayList<>();
        activeResponse = null;
    }

    public static void executeRevertibleAction(Action action) {
        revertibleActions.add(action);
        actions.add(action);
        action.fire();
    }

    public static void executeAction(Action action) {
        actions.add(action);
        action.fire();
    }

    public static void revertAllActions() {
        List<Action> reversedActions = new ArrayList<>(revertibleActions);
        Collections.reverse(reversedActions);

        reversedActions.forEach(Action::revert);
    }

    public static void registerResponse(ServiceResponse.ServiceResponseBuilder response) {
        ServiceResponse serviceResponse = response.build();
        LOGGER.info("Received service response: [{}]", serviceResponse);
        responses.add(serviceResponse);
    }

    public static void setActiveResponseAsMostRecent(ServiceResponse.Source source) {
        setActiveResponseAsMostRecent(source, null);
    }

    public static void setActiveResponseAsMostRecent(ServiceResponse.Source source, String resourceName) {
        List<ServiceResponse> filteredResponses = responses.stream()
            .filter(response -> response.getSource() == source)
            .filter(response -> Optional.ofNullable(resourceName)
                .map(name -> response.getResourceName().equals(name))
                .orElse(true))
            .toList();

        if (filteredResponses.isEmpty()) {
            throw new IllegalStateException("No responses for service");
        }

        activeResponse = filteredResponses.get(filteredResponses.size() - 1);
    }

    /**
     * Get the response current marked as active
     *
     * @return The active response
     */
    public static ServiceResponse getActiveResponse() {
        return activeResponse;
    }

    public static <T> void setActiveResource(T resource) {
        setActiveResource(resource.getClass().getName(), resource);
    }

    public static <T> T getActiveResource(Class<T> resourceType) {
        return (T) activeResources.get(resourceType.getName());
    }

    public static <T> void setActiveResource(String key, T value) {
        activeResources.put(key, value);
    }

    public static <T> T getActiveResource(String key) {
        return (T) activeResources.get(key);
    }

}

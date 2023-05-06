package uk.co.zacheryharley.lambdalight.handler;

public class EventResult {
    private final boolean success;
    private final String eventId;
    private final Throwable cause;

    public static EventResult success(String eventId) {
        return new EventResult(true, eventId, null);
    }

    public static EventResult failure(String eventId, Throwable cause) {
        return new EventResult(false, eventId, cause);
    }

    private EventResult(boolean success, String eventId, Throwable cause) {
        this.success = success;
        this.eventId = eventId;
        this.cause = cause;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getEventId() {
        return eventId;
    }

    public Throwable getCause() {
        return cause;
    }
}

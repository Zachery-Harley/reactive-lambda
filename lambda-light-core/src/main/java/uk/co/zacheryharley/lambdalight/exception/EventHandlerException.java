package uk.co.zacheryharley.lambdalight.exception;

public class EventHandlerException extends RuntimeException {

    public EventHandlerException() {
    }

    public EventHandlerException(String message) {
        super(message);
    }

    public EventHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}

package uk.co.zacheryharley.lambdalight.handler;

import jakarta.annotation.Nullable;
import org.springframework.messaging.MessageHeaders;
import reactor.core.publisher.Mono;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.function.Function;

public interface EventHandler<T, V> extends Function<T, V> {

    static Type getFunctionType() {
        throw new IllegalStateException("Function type not implemented");
    }

    default Mono<EventResult> handleEvent(String eventId, String eventBody, MessageHeaders headers, @Nullable Duration timeout) {
        return handleEvent(eventId, eventBody, timeout);
    }

    default Mono<EventResult> handleEvent(String eventId, String eventBody, @Nullable Duration timeout) {
        return handleEvent(eventId, eventBody);
    }

    default Mono<EventResult> handleEvent(String eventId, String eventBody) {
        return Mono.just(EventResult.success(eventId));
    }

}

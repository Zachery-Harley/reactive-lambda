package uk.co.zacheryharley.lambdalight.handler;

import org.springframework.messaging.Message;
import reactor.core.publisher.Mono;
import uk.co.zacheryharley.lambdalight.exception.EventHandlerException;

import java.lang.reflect.Type;
import java.util.UUID;
import java.util.function.Function;

import static org.springframework.core.ResolvableType.forClassWithGenerics;

public class StringEventHandler implements EventHandler<Mono<Message<String>>, Mono<String>> {
    public static Type getFunctionType() {
        return forClassWithGenerics(Function.class,
                forClassWithGenerics(Message.class, forClassWithGenerics(Mono.class, String.class)),
                forClassWithGenerics(Mono.class, String.class)
        ).getType();
    }

    @Override
    public Mono<String> apply(Mono<Message<String>> event) {
        return event
                .flatMap(e -> this.handleEvent(UUID.randomUUID().toString(), e.getPayload(), e.getHeaders(), null))
                .flatMap(result -> result.isSuccess()
                        ? Mono.just(result)
                        : Mono.error(new EventHandlerException("Failed to process event: [%s]".formatted(result.getEventId()), result.getCause())))
                .map(EventResult::getEventId);
    }

}

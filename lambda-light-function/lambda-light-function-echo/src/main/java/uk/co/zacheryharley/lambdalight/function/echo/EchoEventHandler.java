package uk.co.zacheryharley.lambdalight.function.echo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import uk.co.zacheryharley.lambdalight.aws.sqs.handler.SqsEventHandler;
import uk.co.zacheryharley.lambdalight.handler.EventResult;
import uk.co.zacheryharley.lambdalight.monitoring.JsonLoggingMeterRegistry;

public class EchoEventHandler extends SqsEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(EchoEventHandler.class);
    private static final JsonLoggingMeterRegistry registry = new JsonLoggingMeterRegistry();

    public EchoEventHandler() {
        super(false);
    }

    @Override
    public Mono<EventResult> handleEvent(String messageId, String messageBody) {
        return Mono.just(messageId)
            .doOnNext(id -> registry.counter("message.count").increment())
            .doOnNext(id -> registry.counter("message.size", "messageId", messageId).increment(messageBody.length()))
            .doOnNext(LOGGER::info)
            .map(message -> EventResult.success(messageId))
            .doFinally(x -> registry.publish());
    }
}

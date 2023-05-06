package uk.co.zacheryharley.lambdalight.function.echo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import uk.co.zacheryharley.lambdalight.aws.sqs.handler.SqsEventHandler;
import uk.co.zacheryharley.lambdalight.handler.EventResult;

public class EchoEventHandler extends SqsEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(EchoEventHandler.class);

    public EchoEventHandler() {
        super(false);
    }

    @Override
    public Mono<EventResult> handleEvent(String messageId, String messageBody) {
        return Mono.just(messageId)
                .doOnNext(LOGGER::info)
                .map(message -> EventResult.success(messageId));
    }
}

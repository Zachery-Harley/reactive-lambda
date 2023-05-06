package uk.co.zacheryharley.lambdalight.aws.sqs.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.function.adapter.aws.AWSLambdaUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;
import uk.co.zacheryharley.lambdalight.exception.EventHandlerException;
import uk.co.zacheryharley.lambdalight.handler.EventHandler;
import uk.co.zacheryharley.lambdalight.handler.EventResult;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.core.ResolvableType.forClassWithGenerics;

public abstract class SqsEventHandler implements EventHandler<Mono<Message<SQSEvent>>, Mono<SQSBatchResponse>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqsEventHandler.class);
    private final boolean batchProcessResponse;

    public SqsEventHandler(boolean batchProcessResponse) {
        this.batchProcessResponse = batchProcessResponse;
    }

    public static Type getFunctionType() {
        return forClassWithGenerics(Function.class,
                forClassWithGenerics(Mono.class, forClassWithGenerics(Message.class, SQSEvent.class)),
                forClassWithGenerics(Mono.class, SQSBatchResponse.class)
        ).getType();
    }

    @Override
    public Mono<SQSBatchResponse> apply(Mono<Message<SQSEvent>> sqsEvent) {
        return sqsEvent
                .flatMapMany(event -> Flux.fromIterable(event.getPayload().getRecords())
                        .map(rec -> Tuples.of(event.getHeaders(), rec)))
                .concatMap(record -> handleEvent(record.getT1(), record.getT2()))
                .filter(result -> !result.isSuccess())
                .doOnNext(result -> LOGGER.error("An exception occurred processing event: [{}].", result.getEventId(), result.getCause()))
                .flatMap(result -> batchProcessResponse
                        ? Mono.just(result)
                        : Mono.error(new EventHandlerException("Failed to process event: [%s]".formatted(result.getEventId()), result.getCause())))
                .map(this::toBatchItemFailure)
                .collect(Collectors.toList())
                .map(SQSBatchResponse::new);
    }

    private SQSBatchResponse.BatchItemFailure toBatchItemFailure(EventResult eventResult) {
        return new SQSBatchResponse.BatchItemFailure(eventResult.getEventId());
    }

    private Mono<EventResult> handleEvent(MessageHeaders headers, SQSEvent.SQSMessage message) {
        Duration timeout = Optional.ofNullable(headers.get(AWSLambdaUtils.AWS_CONTEXT, Context.class))
                .map(context -> Duration.ofMillis(context.getRemainingTimeInMillis()))
                .orElse(null);

        return this.handleEvent(message.getMessageId(), message.getBody(), headers, timeout);
    }

}

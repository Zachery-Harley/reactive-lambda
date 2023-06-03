package uk.co.zacheryharley.lambdalight.function.echo;

import com.amazonaws.xray.AWSXRayRecorder;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.entities.Subsegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import uk.co.zacheryharley.lambdalight.aws.sqs.handler.SqsEventHandler;
import uk.co.zacheryharley.lambdalight.handler.EventResult;
import uk.co.zacheryharley.lambdalight.monitoring.JsonLoggingMeterRegistry;

public class EchoEventHandler extends SqsEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(EchoEventHandler.class);
    private static final JsonLoggingMeterRegistry registry = new JsonLoggingMeterRegistry();
    private AWSXRayRecorder awsxRayRecorder = AWSXRayRecorderBuilder.defaultRecorder();

    public EchoEventHandler() {
        super(false);
    }

    @Override
    public Mono<EventResult> handleEvent(String messageId, String messageBody) {
        Subsegment subsegment = awsxRayRecorder.beginSubsegment("EchoHandler");
        return Mono.just(messageId)
            .doOnNext(id -> registry.counter("message.count").increment())
            .doOnNext(id -> registry.counter("message.size", "messageId", messageId).increment(messageBody.length()))
            .doOnNext(m -> LOGGER.info("Running new code: {}", m))
            .doOnNext(m -> LOGGER.info("Running new code: {}", m))
            .doOnNext(m -> LOGGER.info("Running new code: {}", m))
            .doOnNext(m -> LOGGER.info("Running new code: {}", m))
            .doOnNext(m -> LOGGER.info("Running new code: {}", m))
            .doOnNext(m -> LOGGER.info("Running new code: {}", m))
            .doOnNext(m -> LOGGER.info("Running new code: {}", m))
            .doOnNext(m -> LOGGER.info("Running new code: {}", m))
            .doOnNext(m -> LOGGER.info("Running new code: {}", m))
            .doOnNext(m -> LOGGER.info("Running new code: {}", m))
            .flatMap(s -> {
                return s.contains("error")
                    ? Mono.error(new RuntimeException("Forced Exception"))
                    : Mono.just(s);
            })
            .map(message -> EventResult.success(messageId))
            .doOnError(e -> {
                subsegment.setError(true);
                subsegment.addException(e);
            })
            .doFinally(x -> registry.publish())
            .doFinally(x -> awsxRayRecorder.endSubsegment(subsegment));
    }
}

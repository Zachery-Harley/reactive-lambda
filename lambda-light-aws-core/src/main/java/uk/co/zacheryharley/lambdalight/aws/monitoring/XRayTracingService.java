package uk.co.zacheryharley.lambdalight.aws.monitoring;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorder;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.entities.Subsegment;
import org.checkerframework.checker.initialization.qual.Initialized;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.UnknownKeyFor;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

public class XRayTracingService {
    private static AWSXRayRecorder awsxRayRecorder = AWSXRayRecorderBuilder.defaultRecorder();

    public static <T> UnaryOperator<Mono<T>> startTrace(String segmentName) {
        return mono -> mono
            .doOnSubscribe(s -> {
                Subsegment subsegment = awsxRayRecorder.beginSubsegment(segmentName);
            });
    }

    public static <T> UnaryOperator<Mono<T>> stopTrace() {
        AtomicBoolean prematureTermination = new AtomicBoolean(false);
        return mono -> mono
            .doOnError(e -> {
                Subsegment subsegment = awsxRayRecorder.getCurrentSubsegment();
                System.out.println("Ending with exception: " + subsegment.getName());
                subsegment.addException(e);
                subsegment.setError(true);

                awsxRayRecorder.endSubsegment();
                prematureTermination.set(true);
            })
            .doFinally(context -> {
                if (!prematureTermination.get()) {
                    awsxRayRecorder.endSubsegment();
                }
                awsxRayRecorder.getCurrentSubsegment().close();
            });
    }

}

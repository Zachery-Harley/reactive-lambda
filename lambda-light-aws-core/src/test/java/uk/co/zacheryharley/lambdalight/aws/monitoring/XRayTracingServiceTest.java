package uk.co.zacheryharley.lambdalight.aws.monitoring;

import com.amazonaws.xray.AWSXRayRecorder;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.entities.Subsegment;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;

class XRayTracingServiceTest {
    private static AWSXRayRecorder awsxRayRecorder = AWSXRayRecorderBuilder.defaultRecorder();

//
//    @Test
//    void tst() {
//        awsxRayRecorder.beginSegment("Test");
//        awsxRayRecorder.getCurrentSegment().setUser("The User");
//        awsxRayRecorder.getCurrentSegment().setP
//        Mono.just("hello")
//            .transformDeferred(startTrace("Root Trace"))
//            .flatMap(s -> Mono.just(s)
//                .transformDeferred(startTrace("Child Trace"))
//                .doOnNext(value -> System.out.println("Do Thing"))
//                .flatMap(x -> Mono.error(new RuntimeException("Baf")))
//                .transformDeferred(stopTrace()))
//            .transformDeferred(stopTrace())
//            .block();
//    }



}
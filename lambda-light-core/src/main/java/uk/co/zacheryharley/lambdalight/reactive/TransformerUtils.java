package uk.co.zacheryharley.lambdalight.reactive;

import reactor.core.publisher.Mono;

import java.util.function.Function;

public class TransformerUtils {

    private TransformerUtils() {
        // Static only
    }

    public interface MonoToMono<T, R> extends Function<Mono<T>, Mono<R>> {
    }

}

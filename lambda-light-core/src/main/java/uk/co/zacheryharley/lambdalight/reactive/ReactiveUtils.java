package uk.co.zacheryharley.lambdalight.reactive;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class ReactiveUtils {

    private ReactiveUtils() {
        // Static only
    }

    public static <T> UnaryOperator<T> just(Consumer<T> consumer) {
        return t -> {
            consumer.accept(t);
            return t;
        };
    }


}

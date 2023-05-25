package uk.co.zacheryharley.lambdalight.test;

import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.time.Duration;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class RetryUtil {
    private static final int attempts = 10;

    public static RetryTemplate getRetryTemplate(Duration timeout) {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(timeout.toMillis() / attempts);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(attempts);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    public static <T> T retryUntil(Supplier<T> supplier, Predicate<T> condition, Duration timeout) {
        return getRetryTemplate(timeout).execute(x -> {
            T value = supplier.get();
            if (condition.negate().test(value)) {
                throw new RuntimeException("Failed to match condition");
            }
            return value;
        });
    }


}

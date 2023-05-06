package uk.co.zacheryharley.lambdalight.secret;

import reactor.core.publisher.Mono;
import uk.co.zacheryharley.lambdalight.security.SecureString;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SlowSecretProvider implements SecretProvider {
    private final Map<String, SecureString> secrets;
    private final Duration responseTime;
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public SlowSecretProvider(Map<String, SecureString> secrets, Duration responseTime) {
        this.secrets = new ConcurrentHashMap<>(secrets);
        this.responseTime = responseTime;
    }

    public void set(String key, String value) {
        this.secrets.put(key, new SecureString(value));
    }

    @Override
    public Mono<SecureString> getSecret(String secretId) {
        return Mono.fromFuture(getSlowSecret(secretId));
    }

    private CompletableFuture<SecureString> getSlowSecret(String key) {
        CompletableFuture<SecureString> response = new CompletableFuture<>();
        executor.submit(() -> {
            try {
                Thread.sleep(responseTime.toMillis());
                response.complete(secrets.get(key));
            } catch (Exception e) {
                response.completeExceptionally(e);
            }
        });

        return response;
    }
}

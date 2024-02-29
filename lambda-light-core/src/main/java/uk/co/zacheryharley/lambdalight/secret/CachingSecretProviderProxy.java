package uk.co.zacheryharley.lambdalight.secret;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import reactor.core.publisher.Mono;
import uk.co.zacheryharley.lambdalight.security.SecureString;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class CachingSecretProviderProxy implements SecretProvider {
    private final Cache<String, CompletableFuture<SecureString>> cache;
    private final SecretProvider proxy;

    public CachingSecretProviderProxy(SecretProvider proxy, Duration ttl) {
        this.proxy = proxy;
        this.cache = CacheBuilder.newBuilder().expireAfterWrite(ttl).build();
    }

    @Override
    public synchronized Mono<SecureString> getSecret(String secretId) {
        CompletableFuture<SecureString> future = cache.getIfPresent(secretId);
        if (future != null) {
            return Mono.fromFuture(future);
        }

        future = new CompletableFuture<>();
        cache.put(secretId, future);

        return proxy.getSecret(secretId)
            .doOnNext(future::complete)
            .doOnError(future::completeExceptionally);    }
}

package uk.co.zacheryharley.lambdalight.secret;


import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import uk.co.zacheryharley.lambdalight.security.SecureString;

import java.time.Duration;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

class SecretProviderTest {
    private SlowSecretProvider proxied;
    private SecretProvider uut;

    @BeforeEach
    void setUp() {
        proxied = spy(new SlowSecretProvider(ImmutableMap.of(
            "string", new SecureString("value"),
            "map", new SecureString("{\"key\":\"value\", \"key2\":\"value2\"}")
        ), Duration.ofSeconds(1)));

        uut = new CachingSecretProviderProxy(proxied, Duration.ofSeconds(2));
    }

    @Test
    void shouldLoadSecretOnlyOnce() {
        StepVerifier.create(Flux
                .range(0, 100)
                .flatMap(c -> uut.getSecret("string")))
            .expectNextCount(100)
            .verifyComplete();

        then(proxied).should(times(1)).getSecret("string");
    }

    @Test
    void shouldExpireCache() throws InterruptedException {
        StepVerifier.create(uut.getSecret("string"))
            .expectNextMatches(secureString -> secureString.asString().equals("value"))
            .verifyComplete();

        proxied.set("string", "value2");

        StepVerifier.create(uut.getSecret("string"))
            .expectNextMatches(secureString -> secureString.asString().equals("value"))
            .verifyComplete();

        Thread.sleep(Duration.ofSeconds(3).toMillis());

        StepVerifier.create(uut.getSecret("string"))
            .expectNextMatches(secureString -> secureString.asString().equals("value2"))
            .verifyComplete();

        then(proxied).should(times(2)).getSecret("string");
    }

}
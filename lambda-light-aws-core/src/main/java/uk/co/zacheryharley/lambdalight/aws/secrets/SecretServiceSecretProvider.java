package uk.co.zacheryharley.lambdalight.aws.secrets;

import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerAsyncClient;
import uk.co.zacheryharley.lambdalight.secret.SecretProvider;
import uk.co.zacheryharley.lambdalight.security.SecureString;

public class SecretServiceSecretProvider implements SecretProvider {
    private final SecretsManagerAsyncClient client;

    public SecretServiceSecretProvider(SecretsManagerAsyncClient client) {
        this.client = client;
    }

    @Override
    public Mono<SecureString> getSecret(String secretId) {
        return Mono.fromFuture(() -> client.getSecretValue(builder -> builder.secretId(secretId)))
            .map(response -> new SecureString(response.secretString()));
    }

}

package uk.co.zacheryharley.lambdalight.secret;

import reactor.core.publisher.Mono;
import uk.co.zacheryharley.lambdalight.security.SecureString;

public interface SecretProvider {
    Mono<SecureString> getSecret(String secretId);

}

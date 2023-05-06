package uk.co.zacheryharley.lambdalight.aws.secrets.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerAsyncClient;
import uk.co.zacheryharley.lambdalight.aws.config.AwsProperties;
import uk.co.zacheryharley.lambdalight.aws.secrets.SecretServiceSecretProvider;
import uk.co.zacheryharley.lambdalight.secret.CachingSecretProviderProxy;
import uk.co.zacheryharley.lambdalight.secret.SecretProvider;

import java.time.Duration;

public class SecretServiceContextInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecretServiceContextInitializer.class);

    @Override
    public void initialize(GenericApplicationContext context) {
        context.registerBean(SecretsManagerAsyncClient.class,
            () -> secretsManagerAsyncClient(
                context.getBean(AwsCredentialsProvider.class),
                context.getBean(SdkAsyncHttpClient.class),
                context.getBean(AwsProperties.class)
            ),
            definition -> definition.setLazyInit(true));

        context.registerBean(SecretProvider.class,
            () -> secretProvider(
                context.getBean(SecretsManagerAsyncClient.class)
            ));
    }

    public SecretProvider secretProvider(SecretsManagerAsyncClient client) {
        return new CachingSecretProviderProxy(new SecretServiceSecretProvider(client), Duration.ofMinutes(5));
    }

    public SecretsManagerAsyncClient secretsManagerAsyncClient(AwsCredentialsProvider credentialsProvider,
                                                               SdkAsyncHttpClient httpClient,
                                                               AwsProperties awsProperties) {
        LOGGER.info("SecretManagerAsyncClient created!");
        return SecretsManagerAsyncClient.builder()
            .credentialsProvider(credentialsProvider)
            .httpClient(httpClient)
            .region(awsProperties.getRegion())
            .build();
    }
}

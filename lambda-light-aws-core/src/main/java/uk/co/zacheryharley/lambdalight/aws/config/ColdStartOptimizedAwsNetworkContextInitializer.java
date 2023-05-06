package uk.co.zacheryharley.lambdalight.aws.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;

import java.time.Duration;

public class ColdStartOptimizedAwsNetworkContextInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

    @Override
    public void initialize(GenericApplicationContext context) {
        context.registerBean(AwsProperties.class, AwsProperties::new);
        context.registerBean(SdkAsyncHttpClient.class, this::awsAsyncClientProxyConfig);
        context.registerBean(AwsCredentialsProvider.class, this::credentialsProvider);
    }

    public SdkAsyncHttpClient awsAsyncClientProxyConfig() {
        return NettyNioAsyncHttpClient.builder()
                .connectionTimeout(Duration.ofMinutes(15))
                .maxConcurrency(100)
                .build();
    }

    public AwsCredentialsProvider credentialsProvider() {
        return EnvironmentVariableCredentialsProvider.create();
    }


}

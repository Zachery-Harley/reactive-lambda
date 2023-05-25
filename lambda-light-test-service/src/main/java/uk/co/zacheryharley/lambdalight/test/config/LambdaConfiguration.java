package uk.co.zacheryharley.lambdalight.test.config;


import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;

public class LambdaConfiguration {

    public static S3Client getS3Client() {
        return S3Client.builder()
            .httpClient(getHttpClient())
            .credentialsProvider(getCredentialProvider())
            .region(getRegionProvider().getRegion())
            .build();
    }

    public static LambdaClient getLambdaClient() {
        return LambdaClient.builder()
            .httpClient(getHttpClient())
            .credentialsProvider(getCredentialProvider())
            .region(getRegionProvider().getRegion())
            .build();
    }

    public static SqsClient getSqsClient() {
        return SqsClient.builder()
            .httpClient(getHttpClient())
            .credentialsProvider(getCredentialProvider())
            .region(getRegionProvider().getRegion())
            .build();
    }

    public static CloudWatchClient getCloudWatchClient() {
        return CloudWatchClient.builder()
            .httpClient(getHttpClient())
            .credentialsProvider(getCredentialProvider())
            .region(getRegionProvider().getRegion())
            .build();
    }

    private static AwsCredentialsProvider getCredentialProvider() {
        return DefaultCredentialsProvider.create();
    }

    private static AwsRegionProvider getRegionProvider() {
        return DefaultAwsRegionProviderChain.builder().build();
    }

    private static SdkHttpClient getHttpClient() {
        return UrlConnectionHttpClient.create();
    }

}

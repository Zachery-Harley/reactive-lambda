package uk.co.zacheryharley.lambdalight.test.resource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AwsResource {
    private static Map<String, Lambda> lambdas = new ConcurrentHashMap<>();
    private static Map<String, S3Bucket> buckets = new ConcurrentHashMap<>();

    public static Lambda getLambda(String lambdaName) {
        lambdas.computeIfAbsent(lambdaName, Lambda::new);
        return lambdas.get(lambdaName);
    }

    public static S3Bucket getBucket(String bucketName) {
        buckets.computeIfAbsent(bucketName, S3Bucket::new);
        return buckets.get(bucketName);
    }

}

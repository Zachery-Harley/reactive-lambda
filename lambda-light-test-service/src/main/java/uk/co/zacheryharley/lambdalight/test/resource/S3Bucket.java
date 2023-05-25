package uk.co.zacheryharley.lambdalight.test.resource;

import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import uk.co.zacheryharley.lambdalight.test.Action;
import uk.co.zacheryharley.lambdalight.test.actions.s3.UploadObjectAction;

import java.nio.file.Path;

import static uk.co.zacheryharley.lambdalight.test.config.LambdaConfiguration.getS3Client;

public class S3Bucket {
    private final String bucketName;

    public S3Bucket(String bucketName) {
        this.bucketName = bucketName;
    }

    public PutObjectResponse uploadObject(String key, Path filePath) {
        return getS3Client().putObject(request -> request
                .bucket(bucketName)
                .key(key),
            filePath
        );
    }

    public String getName() {
        return bucketName;
    }

    public S3Object getObject(String key) {
        return new S3Object(this, key);
    }

    @Override
    public String toString() {
        return "S3Bucket{" +
            "bucketName='" + bucketName + '\'' +
            '}';
    }

    public Actions actions() {
        return new Actions(this);
    }

    public static class Actions {
        private final S3Bucket bucket;

        public Actions(S3Bucket bucket) {
            this.bucket = bucket;
        }

        public Action uploadObject(String path, String key) {
            return new UploadObjectAction(path, bucket, key);
        }
    }

}

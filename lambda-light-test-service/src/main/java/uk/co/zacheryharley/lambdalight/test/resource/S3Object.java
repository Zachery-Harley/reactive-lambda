package uk.co.zacheryharley.lambdalight.test.resource;

import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.ObjectAttributes;
import uk.co.zacheryharley.lambdalight.test.Action;
import uk.co.zacheryharley.lambdalight.test.MonitorPoint;
import uk.co.zacheryharley.lambdalight.test.actions.s3.DownloadObjectAction;
import uk.co.zacheryharley.lambdalight.test.monitor.s3.ObjectExistsMonitor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static uk.co.zacheryharley.lambdalight.test.config.LambdaConfiguration.getS3Client;

public class S3Object {
    public static final String ACTIVE_RESOURCE_LOCAL_PATH = "s3ObjectLocalPath";

    private final S3Bucket bucket;
    private final String key;

    public S3Object(S3Bucket bucket, String key) {
        this.bucket = bucket;
        this.key = key;
    }

    public S3Bucket getBucket() {
        return bucket;
    }

    public String getKey() {
        return key;
    }

    public boolean exists() {
        try {
            getSize();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public long getSize() {
        return getS3Client().getObjectAttributes(request -> request
                .bucket(bucket.getName())
                .key(key)
                .objectAttributes(ObjectAttributes.OBJECT_SIZE))
            .objectSize();
    }

    public Path download() {
        try {
            Path tempFile = File.createTempFile("s3File", bucket.getName()).toPath();
            Files.delete(tempFile);
            getS3Client().getObject(request -> request
                    .bucket(bucket.getName())
                    .key(key),
                tempFile
            );
            return tempFile;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DeleteObjectResponse delete() {
        return getS3Client().deleteObject(request -> request
            .bucket(bucket.getName())
            .key(key)
        );
    }

    public Actions actions() {
        return new Actions(this);
    }

    public Monitor monitor() {
        return new Monitor(this);
    }

    public static class Actions {
        private final S3Object object;

        public Actions(S3Object object) {
            this.object = object;
        }

        public Action download() {
            return new DownloadObjectAction(object);
        }
    }

    public static class Monitor {
        private final S3Object object;

        public Monitor(S3Object object) {
            this.object = object;
        }

        public MonitorPoint exists() {
            return new ObjectExistsMonitor(object);
        }
    }
}

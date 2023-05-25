package uk.co.zacheryharley.lambdalight.test.actions.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import uk.co.zacheryharley.lambdalight.test.Action;
import uk.co.zacheryharley.lambdalight.test.resource.S3Bucket;

public class UploadObjectAction implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadObjectAction.class);
    private final String localPath;
    private final String key;
    private final S3Bucket bucket;

    public UploadObjectAction(String localPath, S3Bucket bucket, String key) {
        this.localPath = localPath;
        this.key = key;
        this.bucket = bucket;
    }

    @Override
    public boolean fire() {
        LOGGER.info("Triggering Action: UploadObject From Path: [{}]. Bucket: [{}], Key: [{}]", localPath, bucket, key);
        try {
            bucket.uploadObject(key, new ClassPathResource(localPath).getFile().toPath());
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Local file does not exist", e);
        }
    }

    @Override
    public boolean revert() {
        LOGGER.info("Reverting Action: UploadObject. Bucket: [{}], Key: [{}]", bucket, key);
        bucket.getObject(key).delete();
        return true;
    }
}

package uk.co.zacheryharley.lambdalight.test.actions.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.zacheryharley.lambdalight.test.Action;
import uk.co.zacheryharley.lambdalight.test.TestContext;
import uk.co.zacheryharley.lambdalight.test.resource.S3Object;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static uk.co.zacheryharley.lambdalight.test.TestContext.setActiveResource;

public class DownloadObjectAction implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadObjectAction.class);
    private final S3Object object;
    private Path path;

    public DownloadObjectAction(S3Object object) {
        this.object = object;
    }

    @Override
    public boolean fire() {
        LOGGER.info("Triggering Action: DownloadObject Bucket: [{}], Key: [{}]", object.getBucket().getName(), object.getKey());
        this.path = object.download();
        setActiveResource(S3Object.ACTIVE_RESOURCE_LOCAL_PATH, path);
        return true;
    }

    @Override
    public boolean revert() {
        try {
            LOGGER.info("Reverting Action: DownloadObject LocalPath: [{}], Bucket: [{}], Key: [{}]", path, object.getBucket().getName(), object.getKey());
            Files.delete(path);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

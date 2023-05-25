package uk.co.zacheryharley.lambdalight.test.monitor.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.zacheryharley.lambdalight.test.MonitorPoint;
import uk.co.zacheryharley.lambdalight.test.resource.S3Object;

import java.util.concurrent.atomic.AtomicInteger;

public class ObjectExistsMonitor implements MonitorPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectExistsMonitor.class);
    private final S3Object object;
    private final AtomicInteger lastValue = new AtomicInteger();


    public ObjectExistsMonitor(S3Object object) {
        this.object = object;
    }

    @Override
    public int refresh() {
        LOGGER.info("Refreshing Monitor Point: Object exists. Bucket: [{}], Key: [{}]", object.getBucket().getName(), object.getKey());
        this.lastValue.set(object.exists() ? 1 : 0);
        return lastValue.get();
    }

    @Override
    public int lastValue() {
        return lastValue.get();
    }

}

package uk.co.zacheryharley.lambdalight.monitoring.instrument;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Similar to the offerings from Micrometer however designed for short-lived services such as AWS lambda where the
 * time between metric publishing is inconstant
 */
public interface Timer {

    Snapshot getSnapshot();

    void record(Duration duration);

}

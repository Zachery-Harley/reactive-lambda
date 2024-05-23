package uk.co.zacheryharley.lambdalight.monitoring.instrument;

import java.time.Duration;

/**
 * Similar to the offerings from Micrometer however designed for short-lived services such as AWS lambda where the
 * time between metric publishing is inconstant
 */
public interface Timer extends Meter {

    Snapshot getSnapshot();

    void record(Duration duration);

}

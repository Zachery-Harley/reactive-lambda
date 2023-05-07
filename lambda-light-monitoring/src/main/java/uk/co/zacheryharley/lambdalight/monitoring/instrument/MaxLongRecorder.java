package uk.co.zacheryharley.lambdalight.monitoring.instrument;

import java.util.concurrent.atomic.AtomicLong;

public class MaxLongRecorder {
    private final AtomicLong max = new AtomicLong();

    public void record(long sample) {
        max.getAndUpdate(existing -> Math.max(sample, existing));
    }

    public long getAndReset() {
        return max.getAndSet(0);
    }

}

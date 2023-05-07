package uk.co.zacheryharley.lambdalight.monitoring.instrument;

import java.time.Duration;
import java.util.concurrent.atomic.LongAdder;

public class DefaultTimer implements Timer {
    private final LongAdder total = new LongAdder();
    private final LongAdder count = new LongAdder();
    private final MaxLongRecorder max = new MaxLongRecorder();

    public Snapshot getSnapshot() {
        return new Snapshot(
            total.sumThenReset(),
            count.sumThenReset(),
            max.getAndReset()
        );
    }

    @Override
    public void record(Duration duration) {
        long timeNano = duration.toNanos();
        this.total.add(timeNano);
        this.count.increment();
        max.record(timeNano);
    }
}

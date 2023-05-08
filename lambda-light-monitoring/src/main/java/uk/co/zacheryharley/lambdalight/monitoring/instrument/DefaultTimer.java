package uk.co.zacheryharley.lambdalight.monitoring.instrument;

import java.time.Duration;
import java.util.concurrent.atomic.LongAdder;

public class DefaultTimer implements Timer {
    private final Meter.Id id;
    private final LongAdder total = new LongAdder();
    private final LongAdder count = new LongAdder();
    private final MaxLongRecorder max = new MaxLongRecorder();

    public DefaultTimer(Id id) {
        this.id = id;
    }

    public synchronized Snapshot getSnapshot() {
        return new Snapshot(
            count.sumThenReset(),
            total.sumThenReset(),
            max.getAndReset()
        );
    }

    @Override
    public synchronized void record(Duration duration) {
        long timeNano = duration.toNanos();
        this.total.add(timeNano);
        this.count.increment();
        max.record(timeNano);
    }

    @Override
    public Id getId() {
        return this.id;
    }
}

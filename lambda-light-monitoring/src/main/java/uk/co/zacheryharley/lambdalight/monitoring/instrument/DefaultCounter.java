package uk.co.zacheryharley.lambdalight.monitoring.instrument;

import java.util.concurrent.atomic.LongAdder;

public class DefaultCounter implements Counter {
    private final Meter.Id id;
    private final LongAdder total = new LongAdder();

    public DefaultCounter(Id id) {
        this.id = id;
    }

    @Override
    public void increment() {
        this.total.increment();
    }

    @Override
    public void increment(long amount) {
        this.total.add(amount);
    }

    @Override
    public long getAndReset() {
        return this.total.sumThenReset();
    }

    @Override
    public Id getId() {
        return this.id;
    }
}

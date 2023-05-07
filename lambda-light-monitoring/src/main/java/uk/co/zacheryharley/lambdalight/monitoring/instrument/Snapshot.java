package uk.co.zacheryharley.lambdalight.monitoring.instrument;

import java.util.concurrent.TimeUnit;

public class Snapshot {
    private final long count;
    private final long total;
    private final long max;

    public Snapshot(long count, long total, long max) {
        this.count = count;
        this.total = total;
        this.max = max;
    }

    public long getCount() {
        return count;
    }

    public long getTotal() {
        return total;
    }

    public long getTotal(TimeUnit unit) {
        return unit.convert(total, TimeUnit.NANOSECONDS);
    }

    public long getMax() {
        return max;
    }

    public long getMax(TimeUnit unit) {
        return unit.convert(total, TimeUnit.NANOSECONDS);
    }
}

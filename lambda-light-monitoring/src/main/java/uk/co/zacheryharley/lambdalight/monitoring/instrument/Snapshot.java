package uk.co.zacheryharley.lambdalight.monitoring.instrument;

import java.util.concurrent.TimeUnit;

public record Snapshot(long count, long total, long max) {

    public long total(TimeUnit unit) {
        return unit.convert(total, TimeUnit.NANOSECONDS);
    }

    public long max(TimeUnit unit) {
        return unit.convert(max, TimeUnit.NANOSECONDS);
    }

    public double mean() {
        return mean(TimeUnit.NANOSECONDS);
    }

    public double mean(TimeUnit unit) {
        if (count == 0) {
            return 0;
        }
        return (double) total(unit) / count();
    }
}

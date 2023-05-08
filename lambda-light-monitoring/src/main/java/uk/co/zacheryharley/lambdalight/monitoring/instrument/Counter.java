package uk.co.zacheryharley.lambdalight.monitoring.instrument;

import java.util.concurrent.atomic.AtomicLong;

public interface Counter extends Meter {

    void increment();

    void increment(long amount);

    long getAndReset();

}

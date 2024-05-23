package uk.co.zacheryharley.lambdalight.monitoring.instrument;

public interface Counter extends Meter {

    void increment();

    void increment(long amount);

    long getAndReset();

}

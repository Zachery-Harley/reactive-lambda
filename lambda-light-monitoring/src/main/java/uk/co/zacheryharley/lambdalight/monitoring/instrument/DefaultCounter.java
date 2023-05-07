package uk.co.zacheryharley.lambdalight.monitoring.instrument;

public class DefaultCounter implements Counter {

    @Override
    public void increment() {

    }

    @Override
    public void increment(long amount) {

    }

    @Override
    public long getAndReset() {
        return 0;
    }
}

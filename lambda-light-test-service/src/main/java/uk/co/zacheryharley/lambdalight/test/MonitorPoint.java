package uk.co.zacheryharley.lambdalight.test;

public interface MonitorPoint {

    int refresh();

    int lastValue();

    default BlockingMonitorPoint until() {
        return new BlockingMonitorPoint(this);
    }

}

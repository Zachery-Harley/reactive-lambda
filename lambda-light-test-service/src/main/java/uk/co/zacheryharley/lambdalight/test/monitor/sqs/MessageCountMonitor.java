package uk.co.zacheryharley.lambdalight.test.monitor.sqs;

import uk.co.zacheryharley.lambdalight.test.MonitorPoint;
import uk.co.zacheryharley.lambdalight.test.resource.SqsQueue;

import java.util.concurrent.atomic.AtomicInteger;

public class MessageCountMonitor implements MonitorPoint {
    private final SqsQueue queue;
    private final AtomicInteger lastValue = new AtomicInteger(0);

    public MessageCountMonitor(SqsQueue queue) {
        this.queue = queue;
    }

    @Override
    public int refresh() {
        lastValue.set(queue.getNumberOfMessages());
        return lastValue();
    }

    @Override
    public int lastValue() {
        return lastValue.get();
    }

}

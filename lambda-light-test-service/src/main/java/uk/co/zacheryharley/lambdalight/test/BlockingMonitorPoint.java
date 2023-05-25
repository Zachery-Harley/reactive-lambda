package uk.co.zacheryharley.lambdalight.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.function.Predicate;

public class BlockingMonitorPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlockingMonitorPoint.class);
    private final MonitorPoint monitorPoint;
    private Predicate<MonitorPoint> condition;

    public BlockingMonitorPoint(MonitorPoint monitorPoint) {
        this.monitorPoint = monitorPoint;
    }

    public BlockingMonitorPoint havingValue(boolean value) {
        return havingValue(value ? 1 : 0);
    }

    public BlockingMonitorPoint havingValue(int value) {
        this.condition = m -> m.lastValue() == value;
        return this;
    }

    public boolean wait(Duration timeout) {
        return RetryUtil.getRetryTemplate(timeout).execute(context -> {
            monitorPoint.refresh();
            if (condition.test(monitorPoint)) {
                LOGGER.info("Blocking Monitor Point: Condition met, current value: {}", monitorPoint.lastValue());
                return true;
            }
            LOGGER.info("Blocking Monitor Point: Condition not met, current value: {}", monitorPoint.lastValue());
            throw new RuntimeException("Condition not met");
        });
    }


}

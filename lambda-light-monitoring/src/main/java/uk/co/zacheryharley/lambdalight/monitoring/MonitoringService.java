package uk.co.zacheryharley.lambdalight.monitoring;

import uk.co.zacheryharley.lambdalight.monitoring.instrument.Counter;
import uk.co.zacheryharley.lambdalight.monitoring.instrument.Gauge;
import uk.co.zacheryharley.lambdalight.monitoring.instrument.Timer;

import java.util.Collection;
import java.util.Map;
import java.util.function.DoubleSupplier;

public interface MonitoringService {

    void publish();

    Timer timer(String name, Tags tags);

    Timer timer(String name, String... tags);

    Counter counter(String name, Tags tags);

    Counter counter(String name, String... tags);

    Gauge gauge(DoubleSupplier function, String name, Tags tags);

    Gauge gauge(DoubleSupplier function, String name, String... tags);

    Gauge gauge(Collection<?> collection, String name, String... tags);

    Gauge gauge(Collection<?> collection, String name, Tags tags);

    Gauge gauge(Map<?, ?> map, String name, String... tags);

    Gauge gauge(Map<?, ?> map, String name, Tags tags);

}

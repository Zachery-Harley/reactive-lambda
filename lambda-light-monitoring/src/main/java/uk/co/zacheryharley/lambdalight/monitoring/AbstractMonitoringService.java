package uk.co.zacheryharley.lambdalight.monitoring;

import uk.co.zacheryharley.lambdalight.monitoring.instrument.*;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.DoubleSupplier;

public abstract class AbstractMonitoringService implements MonitoringService {
    protected final Map<Meter.Id, Meter> meters = new ConcurrentHashMap<>();
    protected final TimeUnit baseTimeUnit = TimeUnit.MILLISECONDS;


    @Override
    public Timer timer(String name, Tags tags) {
        Meter.Id id = new Meter.Id(name, tags, "timer");
        meters.computeIfAbsent(id, DefaultTimer::new);
        return (Timer) meters.get(id);
    }

    @Override
    public Timer timer(String name, String... tags) {
        return timer(name, new Tags(tags));
    }

    @Override
    public Counter counter(String name, Tags tags) {
        Meter.Id id = new Meter.Id(name, tags, "counter");
        meters.computeIfAbsent(id, DefaultCounter::new);
        return (Counter) meters.get(id);
    }

    @Override
    public Counter counter(String name, String... tags) {
        return counter(name, new Tags(tags));
    }

    @Override
    public Gauge gauge(DoubleSupplier function, String name, Tags tags) {
        Meter.Id id = new Meter.Id(name, tags, "gauge");
        meters.computeIfAbsent(id, key -> new DefaultGauge(key, function));
        return (Gauge) meters.get(id);
    }

    @Override
    public Gauge gauge(DoubleSupplier function, String name, String... tags) {
        return gauge(function, name, new Tags(tags));
    }

    @Override
    public Gauge gauge(Collection<?> collection, String name, String... tags) {
        return gauge(collection, name, new Tags(tags));
    }

    @Override
    public Gauge gauge(Collection<?> collection, String name, Tags tags) {
        return gauge(collection::size, name, tags);
    }

    @Override
    public Gauge gauge(Map<?, ?> map, String name, String... tags) {
        return gauge(map, name, new Tags(tags));
    }

    @Override
    public Gauge gauge(Map<?, ?> map, String name, Tags tags) {
        return gauge(map::size, name, tags);
    }
}

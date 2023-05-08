package uk.co.zacheryharley.lambdalight.monitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.co.zacheryharley.lambdalight.monitoring.instrument.*;

import java.util.function.Consumer;

/**
 * Similar to Micrometers LoggingMeterRegistry, however unlike the Micrometer implementation calling publish will
 * always cause all Meters to publish their result, regardless of steps. Each meter will also be reset when publishing
 * its current value allowing for short-lived services to do a final and forced publish before shutting down guaranteeing
 * all metrics are published.
 */
public class JsonLoggingMeterRegistry extends AbstractMonitoringService {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final Consumer<String> loggingSink;

    public JsonLoggingMeterRegistry() {
        this(System.out::println);
    }

    public JsonLoggingMeterRegistry(Consumer<String> loggingSink) {
        this.loggingSink = loggingSink;
    }

    @Override
    public void publish() {
        meters.forEach((id, meter) -> {
            JsonBuilder jsonBuilder = new JsonBuilder(meter);
            meter.apply(
                counter -> writeCounter(jsonBuilder, counter),
                timer -> writeTimer(jsonBuilder, timer),
                gauge -> writeGauge(jsonBuilder, gauge),
                unknown -> loggingSink.accept("Unknown meter type: %s".formatted(unknown.getClass()))
            );
        });
    }

    private void writeCounter(JsonBuilder json, Counter counter) {
        loggingSink.accept(json
            .withCount(counter.getAndReset())
            .build());
    }

    private void writeTimer(JsonBuilder json, Timer timer) {
        Snapshot snapshot = timer.getSnapshot();
        loggingSink.accept(json
            .withCount(snapshot.count())
            .withTotal(snapshot.total(baseTimeUnit))
            .withMax(snapshot.max(baseTimeUnit))
            .withValue("unit", baseTimeUnit.toString())
            .withMean(snapshot.mean(baseTimeUnit))
            .build());
    }

    private void writeGauge(JsonBuilder json, Gauge gauge) {
        loggingSink.accept(json
            .withValue("value", gauge.getValue())
            .build());
    }

    public static class JsonBuilder {
        private final ObjectNode meterNode;

        public JsonBuilder(Meter meter) {
            this.meterNode = mapper.createObjectNode();
            ObjectNode tags = mapper.createObjectNode();

            meter.getId().getTags().forEach(tag -> tags.put(tag.key(), tag.value()));
            meterNode.put("name", meter.getId().getName());
            meterNode.put("type", meter.getId().getType());
            meterNode.set("tags", tags);
        }

        public JsonBuilder withCount(long count) {
            return withValue("count", count);
        }

        public JsonBuilder withTotal(double total) {
            return withValue("total", total);
        }

        public JsonBuilder withMax(double max) {
            return withValue("max", max);
        }

        public JsonBuilder withMean(double mean) {
            return withValue("mean", mean);
        }

        public JsonBuilder withValue(String key, long value) {
            meterNode.put(key, value);
            return this;
        }

        public JsonBuilder withValue(String key, double value) {
            meterNode.put(key, value);
            return this;
        }

        public JsonBuilder withValue(String key, String value) {
            meterNode.put(key, value);
            return this;
        }

        public String build() {
            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.set("meter", this.meterNode);
            return rootNode.toString();
        }
    }


}

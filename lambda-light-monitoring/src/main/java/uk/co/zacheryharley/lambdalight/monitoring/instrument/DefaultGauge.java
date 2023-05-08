package uk.co.zacheryharley.lambdalight.monitoring.instrument;

import java.util.function.DoubleSupplier;

public class DefaultGauge implements Gauge {
    private final Meter.Id id;
    private final DoubleSupplier valueProvider;

    public DefaultGauge(Id id, DoubleSupplier valueProvider) {
        this.id = id;
        this.valueProvider = valueProvider;
    }

    @Override
    public double getValue() {
        return valueProvider.getAsDouble();
    }

    @Override
    public Id getId() {
        return this.id;
    }
}

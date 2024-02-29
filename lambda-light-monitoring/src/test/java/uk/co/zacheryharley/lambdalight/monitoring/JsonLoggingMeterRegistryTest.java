package uk.co.zacheryharley.lambdalight.monitoring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.zacheryharley.lambdalight.monitoring.instrument.Counter;
import uk.co.zacheryharley.lambdalight.monitoring.instrument.Timer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class JsonLoggingMeterRegistryTest {
    private JsonLoggingMeterRegistry uut;
    private AtomicReference<String> actual;

    @BeforeEach
    void setUp() {
        this.actual = new AtomicReference<>("");
        this.uut = new JsonLoggingMeterRegistry(actual::set);
    }

    @Test
    void shouldCreateCounter() {
        String expected = "{\"meter\":{\"name\":\"my.counter\",\"type\":\"counter\",\"tags\":{\"tag1\":\"value1\"},\"count\":%s}}";
        Counter counter = uut.counter("my.counter", "tag1", "value1");
        counter.increment();
        counter.increment(2);

        uut.publish();
        assertThat(actual.get(), is(expected.formatted(3)));

        uut.publish();
        assertThat(actual.get(), is(expected.formatted(0)));
    }

    @Test
    void shouldCreateTimer() {
        String expected = "{\"meter\":{\"name\":\"my.timer\",\"type\":\"timer\",\"tags\":{\"tag1\":\"value1\"},\"count\":%s,\"total\":%s,\"max\":%s,\"unit\":\"MILLISECONDS\",\"mean\":%s}}";
        Timer timer = uut.timer("my.timer", "tag1", "value1");
        timer.record(Duration.ofSeconds(3));
        timer.record(Duration.ofSeconds(5));

        uut.publish();
        assertThat(actual.get(), is(expected.formatted(2, 8000D, 5000D, 4000D)));

        uut.publish();
        assertThat(actual.get(), is(expected.formatted(0, 0D, 0D, 0D)));
    }

    @Test
    void shouldCreateGauge() {
        String expected = "{\"meter\":{\"name\":\"my.gauge\",\"type\":\"gauge\",\"tags\":{\"tag1\":\"value1\"},\"value\":%s}}";
        List<String> monitored = new ArrayList<>();
        uut.gauge(monitored, "my.gauge", "tag1", "value1");

        monitored.add("Hello");
        uut.publish();
        assertThat(actual.get(), is(expected.formatted(1D)));

        monitored.add("World");
        monitored.add("!");
        uut.publish();
        assertThat(actual.get(), is(expected.formatted(3D)));
    }


}
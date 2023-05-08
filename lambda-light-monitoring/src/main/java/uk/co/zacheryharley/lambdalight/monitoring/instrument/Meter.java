package uk.co.zacheryharley.lambdalight.monitoring.instrument;

import uk.co.zacheryharley.lambdalight.monitoring.Tag;
import uk.co.zacheryharley.lambdalight.monitoring.Tags;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public interface Meter {

    Id getId();

    default void apply(
        Consumer<Counter> counter,
        Consumer<Timer> timer,
        Consumer<Gauge> gauge,
        Consumer<Object> unknown
    ) {
        if (this instanceof Counter c) {
            counter.accept(c);
        } else if (this instanceof Timer t) {
            timer.accept(t);
        } else if (this instanceof Gauge g) {
            gauge.accept(g);
        } else {
            unknown.accept(this);
        }
    }

    class Id {
        private final String name;
        private final Tags tags;
        private final String type;

        public Id(String name, Tags tags, String type) {
            this.name = name;
            this.tags = tags;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public List<Tag> getTags() {
            return tags.getTags();
        }

        public String getType() {
            return type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id id = (Id) o;
            return Objects.equals(name, id.name) && Objects.equals(tags, id.tags) && Objects.equals(type, id.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, tags, type);
        }
    }

}

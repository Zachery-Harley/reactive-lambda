package uk.co.zacheryharley.lambdalight.monitoring;

import java.util.Objects;

public record Tag(String key, String value) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(key, tag.key) && Objects.equals(value, tag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}

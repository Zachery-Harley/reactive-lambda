package uk.co.zacheryharley.lambdalight.monitoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tags {
    private final List<Tag> tags;

    public Tags(List<Tag> tags) {
        this.tags = new ArrayList<>(tags);
    }

    public Tags(String... tags) {
        List<Tag> tagList = new ArrayList<>();
        for (int i = 0; i < tags.length; i += 2) {
            tagList.add(new Tag(tags[i], tags[i + 1]));
        }
        this.tags = tagList;
    }

    public Tags and(Tag tag) {
        this.tags.add(tag);
        return this;
    }

    public Tags and(String key, String value) {
        return this.and(new Tag(key, value));
    }

    public List<Tag> getTags() {
        return new ArrayList<>(tags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tags tags1 = (Tags) o;
        return Objects.equals(tags, tags1.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tags);
    }
}

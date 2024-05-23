package uk.co.zacheryharley.lambdalight.lock;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.util.UUID;

public class Lock {
    private final String lockId;
    private Instant expiry;

    public Lock(String lockId, Instant expiry) {
        this.lockId = lockId;
        this.expiry = expiry;
    }

    public Lock() {
        this.lockId = UUID.randomUUID().toString();
    }

    public Lock(String lockId) {
        this.lockId = lockId;
    }

    public String getLockId() {
        return lockId;
    }

    public Instant getExpiry() {
        return expiry;
    }

    @JsonIgnore
    public boolean hasLock() {
        return expiry != null &&
            Instant.now().toEpochMilli() < expiry.toEpochMilli();
    }

    @JsonIgnore
    public Lock refresh(Instant expiry) {
        this.expiry = expiry;
        return this;
    }
}

package uk.co.zacheryharley.lambdalight.aws.s3.lock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import uk.co.zacheryharley.lambdalight.json.JsonUtils;
import uk.co.zacheryharley.lambdalight.lock.Lock;

import java.time.Duration;
import java.time.Instant;

public class S3DistributedLock {
    private static final Logger LOGGER = LoggerFactory.getLogger(S3DistributedLock.class);
    private String bucketName = "upload-test-bucket-001";
    private static final String KEY_ROOT = "service/locks/";
    private S3AsyncClient s3AsyncClient;

    public S3DistributedLock(S3AsyncClient s3AsyncClient) {
        this.s3AsyncClient = s3AsyncClient;
    }

    //This works, but nomincation would be better
    public Mono<Lock> tryLock(Lock lock, String name) {
        return get(name)
            .doOnSubscribe(next -> LOGGER.info("Attempting to fetch lock. ClientId: {}", lock.getLockId()))
            .flatMap(currentLock -> {
                if (currentLock.hasLock()) {
                    return currentLock.getLockId().equals(lock.getLockId())
                        ? put(name, lock) //Refresh the lock
                        : Mono.empty(); //We dont have the lock
                } else {
                    return put(name, lock);
                }
            })

            //

            .delayElement(Duration.ofMillis(250))

            //Do we still have the lock or did we lose it
            .flatMap(key -> get(name))
            .flatMap(currentLock -> {
                if (currentLock.hasLock()) {
                    return currentLock.getLockId().equals(lock.getLockId())
                        ? put(name, lock) //Refresh the lock
                        : Mono.empty(); //We dont have the lock
                } else {
                    return put(name, lock);
                }
            })

            .delayElement(Duration.ofMillis(250))

            .flatMap(key -> get(name))
            .map(currentLock -> lock.refresh(
                currentLock.getLockId().equals(lock.getLockId()) //If its our lock refresh and return, otherwise null
                    ? currentLock.getExpiry()
                    : null
            ));
    }

    private Mono<Lock> get(String name) {
        String lockKey = "%s/%s.lock".formatted(KEY_ROOT, name);
        return Mono.fromFuture(() -> s3AsyncClient.getObject(request -> request
                    .bucket(bucketName)
                    .key(lockKey),
                AsyncResponseTransformer.toPublisher()))
            .flatMapMany(Flux::from)
            .map(s -> {
                byte[] bytes = new byte[s.remaining()];
                s.get(bytes);
                return bytes;
            })
            .map(String::new)
            .reduce((s1, s2) -> s1 + s2)
            .map(this::toLock)
            .onErrorResume(e -> Mono.just(new Lock("No one has the lock, this should be improved")));
    }

    private Mono<String> put(String name, Lock lock) {
        String lockKey = "%s/%s.lock".formatted(KEY_ROOT, name);
        return Mono.fromFuture(() -> s3AsyncClient.putObject(request -> request
                    .bucket(bucketName)
                    .key(lockKey)
                    .expires(Instant.now().plusSeconds(10)),
                AsyncRequestBody.fromString(toString(lock.refresh(Instant.now().plusSeconds(10))))))
            .thenReturn(lockKey);
    }

    private Lock toLock(String lock) {
        try {
            return JsonUtils.buildObjectMapper().readValue(lock, Lock.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String toString(Lock lock) {
        try {
            return JsonUtils.buildObjectMapper().writeValueAsString(lock);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}

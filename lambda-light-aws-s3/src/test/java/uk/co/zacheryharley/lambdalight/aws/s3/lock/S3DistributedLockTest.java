package uk.co.zacheryharley.lambdalight.aws.s3.lock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import uk.co.zacheryharley.lambdalight.lock.Lock;

class S3DistributedLockTest {
    private S3AsyncClient client;
    private S3DistributedLock uut;

    @BeforeEach
    void setUp() {
        client = S3AsyncClient.builder()
            .credentialsProvider(ProfileCredentialsProvider.create("dev"))
            .build();

        uut = new S3DistributedLock(client);
    }

    @Test
    void shouldHandleLock() {
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            .parallel(40)
            .runOn(Schedulers.parallel())
            .flatMap(value -> uut.tryLock(new Lock(), "test"))
            .sequential()
            .doOnNext(result -> System.out.printf("Got lock?: %s%n", result.hasLock()))
            .blockLast();
    }


}
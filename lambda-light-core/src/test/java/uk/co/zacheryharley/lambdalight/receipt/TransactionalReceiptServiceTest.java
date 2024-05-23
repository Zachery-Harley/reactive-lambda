package uk.co.zacheryharley.lambdalight.receipt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static uk.co.zacheryharley.lambdalight.reactive.ReactiveUtils.just;

class TransactionalReceiptServiceTest {
    private InMemoryReceiptService receiptService;
    private TransactionalReceiptService uut;

    @BeforeEach
    void setUp() {
        receiptService = new InMemoryReceiptService();
        uut = new TransactionalReceiptService(receiptService);
    }

    @Test
    void shouldCommitReceipt() {
        uut.start("customerA", "receiptHashA")
            .doOnNext(t -> uut.addToTransaction("customerA", "fileA"))
            .doOnNext(t -> uut.addToTransaction("customerA", "fileB"))
            .doOnNext(t -> uut.addToTransaction("customerA", "fileC"))
            .flatMap(t -> uut.commit("customerA"))
            .flatMap(t -> uut.start("customerA", "receiptHashA"))
            .as(StepVerifier::create)
            .assertNext(receipt -> {
                assertThat(receipt.getReceipts(), containsInAnyOrder(
                    "fileA", "fileB", "fileC"));
                assertThat(receipt.isComplete(), is(true));
                assertThat(receipt.isPartial(), is(false));
            })
            .verifyComplete();
    }

    @Test
    void shouldCommitPartialReceipt() {
        uut.start("customerA", "receiptHashA")
            .doOnNext(t -> uut.addToTransaction("customerA", "fileA"))
            .doOnNext(t -> uut.markTransactionAsPartial("customerA"))
            .doOnNext(t -> uut.addToTransaction("customerA", "fileC"))
            .flatMap(t -> uut.commit("customerA"))
            .flatMap(t -> uut.start("customerA", "receiptHashA"))
            .as(StepVerifier::create)
            .assertNext(receipt -> {
                assertThat(receipt.getReceipts(), containsInAnyOrder(
                    "fileA", "fileC"));
                assertThat(receipt.isComplete(), is(false));
                assertThat(receipt.isPartial(), is(true));
            })
            .verifyComplete();
    }

    @Test
    void shouldResume() {
        String large = "%s%s%s".formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        uut.start("test")
            .flatMapMany(transaction -> Flux.range(0, 10000))
            .filter(count -> count > 5)
            .map(count -> large + count)
            .map(just(receipt -> uut.addToTransaction("test", receipt)))
            .switchIfEmpty(Mono.just("").doOnNext(n -> uut.markTransactionAsPartial("test")))
            .then(Mono.just(uut))
            .flatMap(t -> uut.commit("test"))
            .block();

        uut.start("test")
            .flatMapMany(transaction -> Flux.range(0, 10000))
            .map(count -> large + count)
            .filter(receipt -> !uut.doesTransactionContain("test", receipt))
            .as(StepVerifier::create)
            .expectNext(large + 0)
            .expectNext(large + 1)
            .expectNext(large + 2)
            .expectNext(large + 3)
            .expectNext(large + 4)
            .expectNext(large + 5)
            .verifyComplete();
    }

}
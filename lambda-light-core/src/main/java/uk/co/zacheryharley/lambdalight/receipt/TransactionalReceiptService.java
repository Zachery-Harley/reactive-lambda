package uk.co.zacheryharley.lambdalight.receipt;

import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import uk.co.zacheryharley.lambdalight.exception.TransactionalReceiptException;
import uk.co.zacheryharley.lambdalight.reactive.ReactiveJson;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.join;
import static uk.co.zacheryharley.lambdalight.reactive.ReactiveUtils.just;

public class TransactionalReceiptService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalReceiptService.class);
    private static final TypeReference<Set<String>> listTypeReference = new TypeReference<>() {
    };
    private final Map<String, TransactionalReceipt> receipts = new ConcurrentHashMap<>();
    private final ReceiptService receiptService;

    public TransactionalReceiptService(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    public void clearInProgressReceipts() {
        if (!receipts.isEmpty()) {
            LOGGER.error("In progress receipts not empty.");
        }
        receipts.values().forEach(TransactionalReceipt::markAsCleared);
        receipts.clear();
    }

    public Mono<TransactionalReceipt> start(String id, String... name) {
        if (receipts.containsKey(id)) {
            return Mono.error(new TransactionalReceiptException("Transactional receipt already exists with id: %s".formatted(id)));
        }

        return Mono.just(new TransactionalReceipt(id, name))
            .flatMap(this::refresh)
            .map(just(receipt -> receipts.put(id, receipt)));
    }

    public Mono<String> commit(String id) {
        return Mono.justOrEmpty(receipts.get(id))
            .flatMap(this::commit);
    }

    public Mono<String> commit(TransactionalReceipt receipt) {
        return Mono.just(receipt.getReceipts())
            .transform(ReactiveJson.asString())
            .flatMap(body -> receiptService.save(receipt.getReceiptName(), body))
            .map(just(name -> receipts.remove(receipt.getId())));
    }

    public String addToTransaction(String id, String receipt) {
        getReceipt(id).addReceipt(receipt);
        return id;
    }

    public boolean doesTransactionContain(String id, String receipt) {
        return getReceipt(id).contains(receipt);
    }

    public String markTransactionAsPartial(String id) {
        getReceipt(id).markAsPartial();
        return id;
    }

    private TransactionalReceipt getReceipt(String id) {
        TransactionalReceipt transactionalReceipt = receipts.get(id);
        if (transactionalReceipt == null) {
            throw new TransactionalReceiptException("Transactional receipt not found with id: %s".formatted(id));
        }
        return transactionalReceipt;
    }

    private Mono<TransactionalReceipt> refresh(TransactionalReceipt receipt) {
        return getTransactionalReceipt(receipt, true)
            .switchIfEmpty(getTransactionalReceipt(receipt, false))
            .defaultIfEmpty(receipt);
    }

    private Mono<TransactionalReceipt> getTransactionalReceipt(TransactionalReceipt receipt, boolean partial) {
        return receiptService.get(receipt.getReceiptName(partial))
            .transform(ReactiveJson.asObject(listTypeReference))
            .map(records -> new TransactionalReceipt(receipt, records, partial));
    }

    public static class TransactionalReceipt {
        private final Set<String> receipts = new HashSet<>();
        private final String id;
        private final String name;
        private boolean partial;
        private boolean complete;
        private boolean cleared;

        protected TransactionalReceipt(String id, String... suffix) {
            this.id = id;
            this.name = "%s/%s".formatted(id, join("/", suffix));

            this.partial = false;
            this.cleared = false;
            this.complete = false;
        }

        protected TransactionalReceipt(TransactionalReceipt other, Set<String> receipts, boolean partial) {
            this.id = other.id;
            this.name = other.name;
            this.receipts.addAll(receipts);
            this.partial = partial;
            this.complete = !partial;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public void addReceipt(String message) {
            assertNotCleared();
            receipts.add(message);
        }

        public void markAsPartial() {
            assertNotCleared();
            this.partial = true;
        }

        public Set<String> getReceipts() {
            return new HashSet<>(receipts);
        }

        public boolean isPartial() {
            return partial;
        }

        public boolean isComplete() {
            return complete;
        }

        public boolean contains(String receipt) {
            return this.receipts.contains(receipt);
        }

        protected String getReceiptName(boolean partial) {
            return partial ? "%s.partial".formatted(getName()) : getName();
        }

        protected String getReceiptName() {
            return getReceiptName(partial);
        }

        protected void markAsCleared() {
            this.cleared = true;
        }

        private void assertNotCleared() {
            if (this.cleared) {
                throw new TransactionalReceiptException("Transactional receipt cleared. Cannot update");
            }
        }

    }
}

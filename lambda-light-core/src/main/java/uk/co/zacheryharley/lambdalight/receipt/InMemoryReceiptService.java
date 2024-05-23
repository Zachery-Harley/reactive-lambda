package uk.co.zacheryharley.lambdalight.receipt;

import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static uk.co.zacheryharley.lambdalight.reactive.ReactiveUtils.just;

public class InMemoryReceiptService implements ReceiptService {
    private final Map<String, String> receipts = new HashMap<>();

    @Override
    public Mono<String> save(String name, String body) {
        return Mono.just(name)
            .map(just(n -> receipts.put(name, body)));
    }

    @Override
    public Mono<String> get(String name) {
        return Mono.justOrEmpty(receipts.get(name));
    }
}

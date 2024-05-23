package uk.co.zacheryharley.lambdalight.receipt;

import reactor.core.publisher.Mono;

public interface ReceiptService {

    Mono<String> save(String name, String body);

    Mono<String> get(String name);

}

package com.samus.ontop.ontoptest.application.ports.out;

import com.samus.ontop.ontoptest.application.domain.PageSupport;
import com.samus.ontop.ontoptest.application.domain.Transaction;
import reactor.core.publisher.Mono;

public interface TransactionPort {
    Mono<Transaction> save(Transaction transaction);

    Mono<Transaction> getById(Integer id);

    Mono<PageSupport<Transaction>> list(String userId, int page, int size);
}

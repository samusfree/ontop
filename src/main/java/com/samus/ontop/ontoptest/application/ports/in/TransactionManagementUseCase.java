package com.samus.ontop.ontoptest.application.ports.in;

import com.samus.ontop.ontoptest.application.domain.PageSupport;
import com.samus.ontop.ontoptest.application.domain.Transaction;
import com.samus.ontop.ontoptest.application.domain.TransactionRequest;
import reactor.core.publisher.Mono;

public interface TransactionManagementUseCase {
    Mono<Transaction> withdraw(TransactionRequest request);

    Mono<PageSupport<Transaction>> list(String userId, int page, int size);
}

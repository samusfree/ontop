package com.samus.ontop.ontoptest.adapters.persistance.repository;

import com.samus.ontop.ontoptest.adapters.persistance.entity.TransactionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionRepository extends R2dbcRepository<TransactionEntity, Integer> {
    Flux<TransactionEntity> findByUserId(String userId, Pageable pageable);

    Mono<Long> countByUserId(String userId);
}

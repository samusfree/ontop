package com.samus.ontop.ontoptest.adapters.persistence.repository;

import com.samus.ontop.ontoptest.adapters.persistence.entity.AccountEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends R2dbcRepository<AccountEntity, Integer> {
    Mono<AccountEntity> findByUserId(String userId);
}

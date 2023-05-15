package com.samus.ontop.ontoptest.application.ports.out;

import com.samus.ontop.ontoptest.application.domain.Account;
import reactor.core.publisher.Mono;

public interface AccountPort {
    Mono<Account> save(Account account);

    Mono<Account> findByUserId(String userId);
}

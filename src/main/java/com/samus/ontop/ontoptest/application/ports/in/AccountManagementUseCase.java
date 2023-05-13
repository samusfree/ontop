package com.samus.ontop.ontoptest.application.ports.in;

import com.samus.ontop.ontoptest.application.domain.Account;
import reactor.core.publisher.Mono;

public interface AccountManagementUseCase {
    Mono<Account> save(Account account);
}

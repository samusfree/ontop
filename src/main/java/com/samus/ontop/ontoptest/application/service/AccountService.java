package com.samus.ontop.ontoptest.application.service;

import com.samus.ontop.ontoptest.application.domain.Account;
import com.samus.ontop.ontoptest.application.ports.in.AccountManagementUseCase;
import com.samus.ontop.ontoptest.application.ports.out.AccountPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AccountService implements AccountManagementUseCase {
    private final AccountPort accountPort;

    @Autowired
    public AccountService(final AccountPort accountPort) {
        this.accountPort = accountPort;
    }

    @Override
    public Mono<Account> save(Account account) {
        return accountPort.save(account);
    }
}

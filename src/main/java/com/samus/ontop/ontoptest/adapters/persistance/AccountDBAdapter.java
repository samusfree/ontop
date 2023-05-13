package com.samus.ontop.ontoptest.adapters.persistance;

import com.samus.ontop.ontoptest.adapters.persistance.mapper.AccountMapper;
import com.samus.ontop.ontoptest.adapters.persistance.repository.AccountRepository;
import com.samus.ontop.ontoptest.application.domain.Account;
import com.samus.ontop.ontoptest.application.domain.exception.AccountAlreadyExistsException;
import com.samus.ontop.ontoptest.application.domain.exception.AccountNotFoundException;
import com.samus.ontop.ontoptest.application.ports.out.AccountPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@Slf4j
public class AccountDBAdapter implements AccountPort {
    private final AccountRepository accountRepository;

    public AccountDBAdapter(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Mono<Account> save(Account account) {
        return accountRepository.save(AccountMapper.toEntity(account)).map(AccountMapper::toAccount).doOnError(e -> {
            log.error(e.getMessage(), e);
            if (e instanceof DuplicateKeyException) {
                throw new AccountAlreadyExistsException(account.userId());
            }
        });
    }

    @Override
    public Mono<Account> findByUserId(String userId) {
        return accountRepository.findByUserId(userId)
                .doOnError(e -> {
                    log.error(e.getMessage(), e);
                    throw new AccountNotFoundException(userId);
                }).doOnSuccess(e -> {
                    if (Objects.isNull(e)) {
                        throw new AccountNotFoundException(userId);
                    }
                })
                .map(AccountMapper::toAccount);
    }
}

package com.samus.ontop.ontoptest.adapters.persistence;

import com.samus.ontop.ontoptest.adapters.persistence.mapper.AccountMapper;
import com.samus.ontop.ontoptest.adapters.persistence.repository.AccountRepository;
import com.samus.ontop.ontoptest.application.domain.Account;
import com.samus.ontop.ontoptest.application.domain.exception.AccountAlreadyExistsException;
import com.samus.ontop.ontoptest.application.domain.exception.AccountNotFoundException;
import com.samus.ontop.ontoptest.common.CommonObjects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.sql.SQLException;

@ExtendWith(MockitoExtension.class)
public class AccountDBAdapterTest {
    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    private AccountDBAdapter accountDBAdapter;
    private String userId = "1";

    @Test
    public void testSave() {
        Account account = CommonObjects.getAccount();
        Mockito.when(accountRepository.save(
                        AccountMapper.toEntity(account)))
                .thenReturn(Mono.just(AccountMapper.toEntity(account)));

        StepVerifier.create(accountDBAdapter.save(CommonObjects.getAccount()))
                .expectNextMatches(e -> e.userId().equals(account.userId()))
                .verifyComplete();
    }

    @Test
    public void testSaveFailed() {
        Account account = CommonObjects.getAccount();
        Mockito.when(accountRepository.save(
                        AccountMapper.toEntity(account)))
                .thenReturn(Mono.error(new SQLException("Mock error")));

        StepVerifier.create(accountDBAdapter.save(CommonObjects.getAccount()))
                .expectErrorMatches(e -> e instanceof SQLException).verify();
    }

    @Test
    public void testSaveDuplicated() {
        Account account = CommonObjects.getAccount();
        Mockito.when(accountRepository.save(
                        AccountMapper.toEntity(account)))
                .thenReturn(Mono.error(new DuplicateKeyException("Mock error")));

        StepVerifier.create(accountDBAdapter.save(CommonObjects.getAccount()))
                .expectErrorMatches(e -> e instanceof AccountAlreadyExistsException).verify();
    }

    @Test
    public void testGetById() {
        Account account = CommonObjects.getAccount();
        Mockito.when(accountRepository.findByUserId(userId))
                .thenReturn(Mono.just(AccountMapper.toEntity(account)));

        StepVerifier.create(accountDBAdapter.findByUserId(userId))
                .expectNextMatches(e -> e.userId().equals(account.userId()))
                .verifyComplete();
    }

    @Test
    public void testGetByIdFailed() {
        Mockito.when(accountRepository.findByUserId(userId))
                .thenReturn(Mono.error(new SQLException("Mock error")));

        StepVerifier.create(accountDBAdapter.findByUserId(userId))
                .expectErrorMatches(e -> e instanceof AccountNotFoundException).verify();
    }

    @Test
    public void testGetByIdNull() {
        Mockito.when(accountRepository.findByUserId(userId))
                .thenReturn(Mono.justOrEmpty(null));

        StepVerifier.create(accountDBAdapter.findByUserId(userId))
                .expectErrorMatches(e -> e instanceof AccountNotFoundException).verify();
    }

}

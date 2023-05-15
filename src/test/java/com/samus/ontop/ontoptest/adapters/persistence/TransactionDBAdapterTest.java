package com.samus.ontop.ontoptest.adapters.persistence;

import com.samus.ontop.ontoptest.adapters.persistence.entity.TransactionEntity;
import com.samus.ontop.ontoptest.adapters.persistence.mapper.AccountMapper;
import com.samus.ontop.ontoptest.adapters.persistence.mapper.TransactionMapper;
import com.samus.ontop.ontoptest.adapters.persistence.repository.AccountRepository;
import com.samus.ontop.ontoptest.adapters.persistence.repository.TransactionRepository;
import com.samus.ontop.ontoptest.application.domain.Transaction;
import com.samus.ontop.ontoptest.application.domain.TransactionStatus;
import com.samus.ontop.ontoptest.common.CommonObjects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.sql.SQLException;
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
public class TransactionDBAdapterTest {
    @Mock
    AccountRepository accountRepository;
    @Mock
    TransactionRepository transactionRepository;
    @InjectMocks
    private TransactionDBAdapter transactionDBAdapter;

    @Test
    public void testSave() {
        Transaction transaction = CommonObjects.getTransaction(true,
                123, "123", TransactionStatus.CREATED);

        Mockito.when(transactionRepository.save(
                        TransactionMapper.toEntity(transaction)))
                .thenReturn(Mono.just(TransactionMapper.toEntity(transaction)));
        Mockito.when(accountRepository.findById(transaction.getAccount().id()))
                .thenReturn(Mono.just(AccountMapper.toEntity(CommonObjects.getAccount())));

        StepVerifier.create(transactionDBAdapter.save(transaction))
                .expectNextMatches(e -> e.getUserId().equals(transaction.getUserId()))
                .verifyComplete();
    }

    @Test
    public void testSaveAccountIdNull() {
        TransactionEntity transactionEntity = TransactionMapper.toEntity(CommonObjects.getTransaction(true,
                123, "123", TransactionStatus.CREATED));
        transactionEntity.setAccount(null);
        transactionEntity.setAccountId(null);

        Mockito.when(transactionRepository.save(transactionEntity))
                .thenReturn(Mono.just(transactionEntity));

        StepVerifier.create(transactionDBAdapter.save(TransactionMapper.toTransaction(transactionEntity)))
                .expectNextMatches(e -> Objects.isNull(e.getAccount()))
                .verifyComplete();
    }

    @Test
    public void testSaveFailed() {
        Transaction transaction = CommonObjects.getTransaction(true,
                123, "123", TransactionStatus.CREATED);

        Mockito.when(transactionRepository.save(
                        TransactionMapper.toEntity(transaction)))
                .thenReturn(Mono.error(new SQLException("Mock error")));

        StepVerifier.create(transactionDBAdapter.save(transaction))
                .expectErrorMatches(e -> e instanceof SQLException).verify();
    }

    @Test
    public void testGetById() {
        Transaction transaction = CommonObjects.getTransaction(true,
                123, "123", TransactionStatus.CREATED);

        Mockito.when(transactionRepository.findById(transaction.getId()))
                .thenReturn(Mono.just(TransactionMapper.toEntity(transaction)));
        Mockito.when(accountRepository.findById(transaction.getAccount().id()))
                .thenReturn(Mono.just(AccountMapper.toEntity(CommonObjects.getAccount())));

        StepVerifier.create(transactionDBAdapter.getById(transaction.getId()))
                .expectNextMatches(e -> e.getUserId().equals(transaction.getUserId()))
                .verifyComplete();
    }

    @Test
    public void testGetByIdAccountIdNull() {
        TransactionEntity transactionEntity = TransactionMapper.toEntity(CommonObjects.getTransaction(true,
                123, "123", TransactionStatus.CREATED));
        transactionEntity.setAccount(null);
        transactionEntity.setAccountId(null);

        Mockito.when(transactionRepository.findById(transactionEntity.getId()))
                .thenReturn(Mono.just(transactionEntity));

        StepVerifier.create(transactionDBAdapter.getById(transactionEntity.getId()))
                .expectNextMatches(e -> Objects.isNull(e.getAccount()))
                .verifyComplete();
    }

    @Test
    public void testGetByIdFailed() {
        Integer transactionId = 1;

        Mockito.when(transactionRepository.findById(transactionId))
                .thenReturn(Mono.error(new SQLException("Mock error")));

        StepVerifier.create(transactionDBAdapter.getById(transactionId))
                .expectErrorMatches(e -> e instanceof SQLException).verify();
    }

    @Test
    public void getList() {
        Transaction transaction = CommonObjects.getTransaction(true, 123,
                "123", TransactionStatus.CREATED);
        PageRequest pageRequest = PageRequest.of(0, 1);

        Mockito.when(transactionRepository.findByUserId(transaction.getUserId(), pageRequest))
                .thenReturn(Flux.just(TransactionMapper.toEntity(transaction)));
        Mockito.when(accountRepository.findById(transaction.getAccount().id()))
                .thenReturn(Mono.just(AccountMapper.toEntity(CommonObjects.getAccount())));
        Mockito.when(transactionRepository.countByUserId(transaction.getUserId())).thenReturn(Mono.just(1L));

        StepVerifier.create(transactionDBAdapter.list(transaction.getUserId(), 1, 1))
                .expectNextMatches(e -> e.totalPages() == 1L)
                .verifyComplete();
    }

    @Test
    public void getListFailed() {
        Transaction transaction = CommonObjects.getTransaction(true, 123,
                "123", TransactionStatus.CREATED);
        PageRequest pageRequest = PageRequest.of(0, 1);

        Mockito.when(transactionRepository.findByUserId(transaction.getUserId(), pageRequest))
                .thenReturn(Flux.error(new SQLException("Mock error")));
        Mockito.when(transactionRepository.countByUserId(transaction.getUserId())).thenReturn(Mono.just(1L));

        StepVerifier.create(transactionDBAdapter.list(transaction.getUserId(), 1, 1))
                .expectErrorMatches(e -> e instanceof SQLException).verify();
    }
}

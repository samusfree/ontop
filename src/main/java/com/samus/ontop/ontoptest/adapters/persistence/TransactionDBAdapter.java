package com.samus.ontop.ontoptest.adapters.persistence;

import com.samus.ontop.ontoptest.adapters.persistence.entity.TransactionEntity;
import com.samus.ontop.ontoptest.adapters.persistence.mapper.AccountMapper;
import com.samus.ontop.ontoptest.adapters.persistence.mapper.TransactionMapper;
import com.samus.ontop.ontoptest.adapters.persistence.repository.AccountRepository;
import com.samus.ontop.ontoptest.adapters.persistence.repository.TransactionRepository;
import com.samus.ontop.ontoptest.application.domain.PageSupport;
import com.samus.ontop.ontoptest.application.domain.Transaction;
import com.samus.ontop.ontoptest.application.ports.out.TransactionPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class TransactionDBAdapter implements TransactionPort {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionDBAdapter(final TransactionRepository transactionRepository, final AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public Mono<Transaction> save(Transaction transaction) {
        return transactionRepository.save(TransactionMapper.toEntity(transaction)).flatMap(transactionEntity -> {
            if (Objects.isNull(transactionEntity.getAccountId())) {
                return Mono.just(transactionEntity);
            }
            return accountRepository.findById(transactionEntity.getAccountId()).map(AccountMapper::toAccount).map(account -> {
                transactionEntity.setAccount(account);
                return transactionEntity;
            });
        }).map(TransactionMapper::toTransaction);
    }

    public Mono<Transaction> getById(Integer id) {
        return transactionRepository.findById(id)
                .flatMap(this::getTransactionWithAccount);
    }

    @Override
    public Mono<PageSupport<Transaction>> list(String userId, int page, int size) {
        return transactionRepository.findByUserId(userId, PageRequest.of(page - 1, size))
                .parallel().flatMap(this::getTransactionWithAccount).sequential().collectList()
                .zipWith(transactionRepository.countByUserId(userId))
                .map(t -> new PageSupport<>(t.getT1(), page, size, t.getT2()));
    }

    private Mono<Transaction> getTransactionWithAccount(final TransactionEntity entity) {
        Transaction transaction = TransactionMapper.toTransaction(entity);

        if (Objects.isNull(entity.getAccountId())) {
            return Mono.just(transaction);
        }

        return accountRepository.findById(entity.getAccountId()).map(result -> {
            transaction.setAccount(AccountMapper.toAccount(result));
            return transaction;
        });
    }
}
package com.samus.ontop.ontoptest.adapters.persistence.mapper;

import com.samus.ontop.ontoptest.adapters.persistence.entity.TransactionEntity;
import com.samus.ontop.ontoptest.application.domain.Transaction;

import java.util.Objects;

public final class TransactionMapper {
    private TransactionMapper() {
    }

    public static Transaction toTransaction(TransactionEntity transactionEntity) {
        return new Transaction(transactionEntity.getId(), transactionEntity.getUserId(), transactionEntity.getAccount(),
                transactionEntity.getAmount(), transactionEntity.getFee(), transactionEntity.getCreationDate(),
                transactionEntity.getUpdateDate(), transactionEntity.getCurrency(),
                transactionEntity.getWalletTransactionId(), transactionEntity.getType(),
                transactionEntity.getPaymentInfoId(), transactionEntity.getStatus());
    }

    public static TransactionEntity toEntity(Transaction transaction) {
        return new TransactionEntity(transaction.getId(), transaction.getUserId(),
                Objects.isNull(transaction.getAccount()) ? null : transaction.getAccount().id(), transaction.getAccount(),
                transaction.getAmount(), transaction.getFee(), transaction.getCurrency(), transaction.getWalletTransactionId(),
                transaction.getCreationDate(), transaction.getUpdateDate(), transaction.getType(), transaction.getPaymentInfoId(),
                transaction.getStatus());
    }
}

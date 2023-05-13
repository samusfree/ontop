package com.samus.ontop.ontoptest.adapters.persistance.mapper;

import com.samus.ontop.ontoptest.adapters.persistance.entity.TransactionEntity;
import com.samus.ontop.ontoptest.application.domain.Transaction;

public class TransactionMapper {
    public static Transaction toTransaction(TransactionEntity transactionEntity) {
        return new Transaction(transactionEntity.getId(), transactionEntity.getUserId(), transactionEntity.getAccount(),
                transactionEntity.getAmount(), transactionEntity.getFee(), transactionEntity.getCreationDate(),
                transactionEntity.getUpdateDate(), transactionEntity.getCurrency(),
                transactionEntity.getWalletTransactionId(), transactionEntity.getType(),
                transactionEntity.getPaymentInfoId(), transactionEntity.getStatus());
    }

    public static TransactionEntity toEntity(Transaction transaction) {
        return new TransactionEntity(transaction.getId(), transaction.getUserId(), transaction.getAccount().id(), transaction.getAccount(),
                transaction.getAmount(), transaction.getFee(), transaction.getCurrency(), transaction.getWalletTransactionId(),
                transaction.getCreationDate(), transaction.getUpdateDate(), transaction.getType(), transaction.getPaymentInfoId(),
                transaction.getStatus());
    }
}

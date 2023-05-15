package com.samus.ontop.ontoptest.adapters.persistence.mapper;

import com.samus.ontop.ontoptest.adapters.persistence.entity.TransactionEntity;
import com.samus.ontop.ontoptest.application.domain.Transaction;
import com.samus.ontop.ontoptest.application.domain.TransactionStatus;
import com.samus.ontop.ontoptest.common.CommonObjects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransactionMapperTest {
    @Test
    public void testToEntity() {
        Transaction transaction = CommonObjects.getTransaction(true, 123,
                "123", TransactionStatus.CREATED);

        TransactionEntity transactionEntity = TransactionMapper.toEntity(transaction);

        Assertions.assertEquals(transaction.getUserId(), transactionEntity.getUserId());
    }
}

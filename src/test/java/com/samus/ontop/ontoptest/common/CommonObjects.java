package com.samus.ontop.ontoptest.common;

import com.samus.ontop.ontoptest.application.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CommonObjects {
    public static Account getAccount() {
        return new Account(1, "1", "Test", "Test", "222", "2",
                "223");
    }

    public static Transaction getTransaction(boolean withUpdateDate, Integer walletTransaction, String paymentInfoId, TransactionStatus status) {
        return new Transaction(1, "1", CommonObjects.getAccount(), new BigDecimal(-100),
                new BigDecimal(10), LocalDate.now(), withUpdateDate ? LocalDate.now() : null, "USD", walletTransaction,
                TransactionType.WITHDRAW, paymentInfoId, status);
    }

    public static TransactionRequest getTransactionRequest() {
        return new TransactionRequest("1",
                new BigDecimal(100), TransactionType.WITHDRAW, "USD");
    }
}

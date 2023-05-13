package com.samus.ontop.ontoptest.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private Integer id;
    private String userId;
    private Account account;
    private BigDecimal amount;
    private BigDecimal fee;
    private LocalDate creationDate;
    private LocalDate updateDate;
    private String currency;
    private Integer walletTransactionId;
    private TransactionType type;
    private String paymentInfoId;
    private TransactionStatus status;
}

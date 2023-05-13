package com.samus.ontop.ontoptest.adapters.persistance.entity;

import com.samus.ontop.ontoptest.application.domain.Account;
import com.samus.ontop.ontoptest.application.domain.TransactionStatus;
import com.samus.ontop.ontoptest.application.domain.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("transaction")
public class TransactionEntity {
    @Id
    private Integer id;
    private String userId;
    private Integer accountId;
    @Transient
    private Account account;
    private BigDecimal amount;
    private BigDecimal fee;
    private String currency;
    private Integer walletTransactionId;
    private LocalDate creationDate;
    private LocalDate updateDate;
    private TransactionType type;
    private String paymentInfoId;
    private TransactionStatus status;
}

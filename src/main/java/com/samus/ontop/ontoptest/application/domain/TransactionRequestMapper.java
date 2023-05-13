package com.samus.ontop.ontoptest.application.domain;

import com.samus.ontop.ontoptest.application.domain.integration.Destination;
import com.samus.ontop.ontoptest.application.domain.integration.PaymentAccount;
import com.samus.ontop.ontoptest.application.domain.integration.PaymentRequest;
import com.samus.ontop.ontoptest.application.domain.integration.Source;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionRequestMapper {
    public static Transaction toNewTransaction(TransactionRequest request, Account account, BigDecimal fee) {
        return new Transaction(null, request.userId(), account, request.amount().multiply(new BigDecimal(-1)),
                fee, LocalDate.now(), null, request.currency(), null, request.type(),
                null, TransactionStatus.CREATED);
    }

    public static PaymentRequest toPaymentRequest(final Account account, TransactionRequest request,
                                                  BigDecimal amount, Source source) {
        Destination destination = new Destination(String.format("%s %s", account.firstName(), account.lastName()),
                new PaymentAccount(account.accountNumber(), request.currency(), account.routingNumber()));
        return new PaymentRequest(source, destination, amount);
    }
}

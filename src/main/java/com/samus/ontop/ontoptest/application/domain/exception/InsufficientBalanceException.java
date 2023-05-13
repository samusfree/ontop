package com.samus.ontop.ontoptest.application.domain.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends OntopException {
    public InsufficientBalanceException(String userId, BigDecimal amount) {
        super(String.format("The balance is insufficient for the amount %f of the userId %s ", amount, userId));
    }
}

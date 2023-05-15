package com.samus.ontop.ontoptest.application.domain.exception;

public class PaymentErrorException extends OntopException {
    public PaymentErrorException(String userId) {
        super(String.format("Error to connect to Payment API in withdraw Transaction for userId %s", userId));
    }
}

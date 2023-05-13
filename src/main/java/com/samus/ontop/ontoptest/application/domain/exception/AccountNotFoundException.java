package com.samus.ontop.ontoptest.application.domain.exception;

public class AccountNotFoundException extends OntopException {
    public AccountNotFoundException(String userId) {
        super("Account not found for userId: " + userId);
    }
}

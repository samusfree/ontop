package com.samus.ontop.ontoptest.application.domain.exception;

public class AccountAlreadyExistsException extends OntopException {
    public AccountAlreadyExistsException(String userId) {
        super(String.format("Account already exists for the userId %s", userId));
    }
}

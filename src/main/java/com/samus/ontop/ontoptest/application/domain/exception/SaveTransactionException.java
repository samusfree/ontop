package com.samus.ontop.ontoptest.application.domain.exception;

public class SaveTransactionException extends OntopException {
    public SaveTransactionException(String userId) {
        super(String.format("Error to save Transaction for userId %s", userId));
    }
}

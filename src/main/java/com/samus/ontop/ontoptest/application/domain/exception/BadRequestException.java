package com.samus.ontop.ontoptest.application.domain.exception;

public class BadRequestException extends OntopException {
    public BadRequestException() {
        super("The request parameters are invalid");
    }
}

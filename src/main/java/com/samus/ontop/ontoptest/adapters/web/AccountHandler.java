package com.samus.ontop.ontoptest.adapters.web;

import com.samus.ontop.ontoptest.application.domain.Account;
import com.samus.ontop.ontoptest.application.domain.exception.BadRequestException;
import com.samus.ontop.ontoptest.application.ports.in.AccountManagementUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class AccountHandler {
    final AccountManagementUseCase accountManagementUseCase;

    @Autowired
    public AccountHandler(final AccountManagementUseCase accountManagementUseCase) {
        this.accountManagementUseCase = accountManagementUseCase;
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(Account.class).flatMap(account ->
                        ServerResponse
                                .status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(accountManagementUseCase.save(account), Account.class))
                .switchIfEmpty(Mono.error(new BadRequestException()));
    }
}

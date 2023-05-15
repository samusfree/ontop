package com.samus.ontop.ontoptest.adapters.web;

import com.samus.ontop.ontoptest.application.domain.Constants;
import com.samus.ontop.ontoptest.application.domain.PageSupport;
import com.samus.ontop.ontoptest.application.domain.Transaction;
import com.samus.ontop.ontoptest.application.domain.TransactionRequest;
import com.samus.ontop.ontoptest.application.domain.exception.BadRequestException;
import com.samus.ontop.ontoptest.application.ports.in.TransactionManagementUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class TransactionHandler {
    final TransactionManagementUseCase transactionManagementUseCase;

    public TransactionHandler(final TransactionManagementUseCase transactionManagementUseCase) {
        this.transactionManagementUseCase = transactionManagementUseCase;
    }

    public Mono<ServerResponse> withDraw(ServerRequest request) {
        return request.bodyToMono(TransactionRequest.class).flatMap(transactionRequest -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(transactionManagementUseCase.withdraw(transactionRequest), Transaction.class))
                .switchIfEmpty(Mono.error(new BadRequestException()));
    }

    public Mono<ServerResponse> list(ServerRequest request) {
        String userId = request.queryParam(Constants.QUERY_PARAM_USERID).orElse(Constants.EMPTY_STRING);
        int page = Integer.parseInt(request.queryParam(Constants.QUERY_PARAM_PAGE).orElse(Constants.DEFAULT_NEGATIVE_INT));
        int size = Integer.parseInt(request.queryParam(Constants.QUERY_PARAM_SIZE).orElse(Constants.DEFAULT_NEGATIVE_INT));

        if (userId.equals(Constants.EMPTY_STRING) || page < 1 || size < 1) {
            throw new BadRequestException();
        }

        return ServerResponse
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(transactionManagementUseCase.list(userId, page, size), PageSupport.class);
    }
}

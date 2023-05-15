package com.samus.ontop.ontoptest.adapters.web;

import com.samus.ontop.ontoptest.application.domain.PageSupport;
import com.samus.ontop.ontoptest.application.domain.Transaction;
import com.samus.ontop.ontoptest.application.domain.TransactionRequest;
import com.samus.ontop.ontoptest.application.domain.TransactionStatus;
import com.samus.ontop.ontoptest.application.domain.exception.AccountNotFoundException;
import com.samus.ontop.ontoptest.application.ports.in.TransactionManagementUseCase;
import com.samus.ontop.ontoptest.common.CommonObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.mock;

public class TransactionRouterTest {
    private TransactionManagementUseCase transactionManagementUseCase;
    private WebTestClient client;

    @BeforeEach
    void setUp() {
        transactionManagementUseCase = mock(TransactionManagementUseCase.class);
        TransactionHandler transactionHandler = new TransactionHandler(transactionManagementUseCase);
        RouterFunction<?> routes = new TransactionRouter().transactionRoutes(transactionHandler);
        client = WebTestClient.bindToRouterFunction(routes)
                .build();
    }


    @Test
    void testWithdraw() {
        TransactionRequest transactionRequest = CommonObjects.getTransactionRequest();
        Mockito.when(transactionManagementUseCase.withdraw(transactionRequest))
                .thenReturn(Mono.just(CommonObjects.getTransaction(true, 123,
                        "123", TransactionStatus.IN_PROGRESS)));
        client.post()
                .uri(uriBuilder -> uriBuilder.path("/transactions/withdraw")
                        .build())
                .bodyValue(transactionRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);
    }

    @Test
    void testWithdrawError() {
        TransactionRequest transactionRequest = CommonObjects.getTransactionRequest();
        Mockito.when(transactionManagementUseCase.withdraw(transactionRequest))
                .thenReturn(Mono.error(new AccountNotFoundException(transactionRequest.userId())));
        client.post()
                .uri(uriBuilder -> uriBuilder.path("/transactions/withdraw")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(transactionRequest)
                .exchange()
                .expectStatus().is5xxServerError().expectBody();
    }

    @Test
    void testList() {
        Transaction transaction = CommonObjects.getTransaction(true, 123,
                "123", TransactionStatus.IN_PROGRESS);
        Mockito.when(transactionManagementUseCase.list(transaction.getUserId(), 1, 1))
                .thenReturn(Mono.just(new PageSupport<>(List.of(transaction), 1, 1, 1L)));
        client.get()
                .uri(uriBuilder -> uriBuilder.path("/transactions/list").queryParam("userId", transaction.getUserId())
                        .queryParam("page", 1).queryParam("size", 1)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.totalPages").isEqualTo(1);
    }

    @Test
    void testListError() {
        Mockito.when(transactionManagementUseCase.list("1", 1, 1))
                .thenReturn(Mono.error(new RuntimeException("Mock error")));
        client.get()
                .uri(uriBuilder -> uriBuilder.path("/transactions/list").queryParam("userId", "1")
                        .queryParam("page", 1).queryParam("size", 1)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError().expectBody();
    }

    @Test
    void testListErrorBadParameters() {
        client.get()
                .uri(uriBuilder -> uriBuilder.path("/transactions/list").queryParam("userId", "")
                        .queryParam("page", 1).queryParam("size", 1)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError().expectBody();
    }

    @Test
    void testListErrorBadParametersSize() {
        client.get()
                .uri(uriBuilder -> uriBuilder.path("/transactions/list").queryParam("userId", "1")
                        .queryParam("page", 1).queryParam("size", 0)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError().expectBody();
    }

    @Test
    void testListErrorBadParametersPage() {
        client.get()
                .uri(uriBuilder -> uriBuilder.path("/transactions/list").queryParam("userId", "1")
                        .queryParam("page", 0).queryParam("size", 1)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError().expectBody();
    }
}

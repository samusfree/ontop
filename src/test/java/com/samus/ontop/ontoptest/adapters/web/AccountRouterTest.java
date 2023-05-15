package com.samus.ontop.ontoptest.adapters.web;

import com.samus.ontop.ontoptest.application.domain.Account;
import com.samus.ontop.ontoptest.application.domain.exception.AccountAlreadyExistsException;
import com.samus.ontop.ontoptest.application.ports.in.AccountManagementUseCase;
import com.samus.ontop.ontoptest.common.CommonObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.mock;

public class AccountRouterTest {
    private AccountManagementUseCase accountManagementUseCase;
    private WebTestClient client;

    @BeforeEach
    void setUp() {
        accountManagementUseCase = mock(AccountManagementUseCase.class);
        AccountHandler accountHandler = new AccountHandler(accountManagementUseCase);
        RouterFunction<?> routes = new AccountRouter().accountRoutes(accountHandler);
        client = WebTestClient.bindToRouterFunction(routes)
                .build();
    }

    @Test
    public void testSave() {
        Account account = CommonObjects.getAccount();
        Mockito.when(accountManagementUseCase.save(account))
                .thenReturn(Mono.just(account));
        client.post()
                .uri(uriBuilder -> uriBuilder.path("/accounts")
                        .build())
                .bodyValue(account)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);
    }

    @Test
    public void testSaveFailed() {
        Account account = CommonObjects.getAccount();
        Mockito.when(accountManagementUseCase.save(account))
                .thenReturn(Mono.error(new AccountAlreadyExistsException(account.userId())));
        client.post()
                .uri(uriBuilder -> uriBuilder.path("/accounts")
                        .build())
                .bodyValue(account)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError().expectBody();
    }
}

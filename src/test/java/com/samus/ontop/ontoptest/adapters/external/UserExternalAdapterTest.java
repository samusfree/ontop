package com.samus.ontop.ontoptest.adapters.external;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.samus.ontop.ontoptest.application.domain.integration.UserBalance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@TestWithResources
@ExtendWith(MockitoExtension.class)
public class UserExternalAdapterTest {
    @Mock
    private WebClient webClientMock;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersMock;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriMock;
    @Mock
    private WebClient.ResponseSpec responseMock;
    @InjectMocks
    UserExternalAdapter userExternalAdapter;
    @GivenJsonResource("balance-ok.json")
    UserBalance userBalance;

    @Test
    public void testGetBalance() {
        String userId = "1";
        Mockito.when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        Mockito.when(requestHeadersUriMock.uri("/wallets/balance?user_id=" + userId)).thenReturn(requestHeadersMock);
        Mockito.when(requestHeadersMock.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersMock);
        Mockito.when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        Mockito.when(responseMock.bodyToMono(UserBalance.class)).thenReturn(Mono.just(userBalance));
        StepVerifier.create(userExternalAdapter.getBalance(userId))
                .expectNextMatches(e -> e.getBalance().equals(userBalance.getBalance()))
                .verifyComplete();
    }

    @Test
    public void testGetBalanceFailed() {
        String userId = "1";
        Mockito.when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        Mockito.when(requestHeadersUriMock.uri("/wallets/balance?user_id=" + userId)).thenReturn(requestHeadersMock);
        Mockito.when(requestHeadersMock.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersMock);
        Mockito.when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        Mockito.when(responseMock.bodyToMono(UserBalance.class)).thenReturn(Mono.error(new RuntimeException("Mock error")));
        StepVerifier.create(userExternalAdapter.getBalance(userId))
                .expectError().verify();
    }
}

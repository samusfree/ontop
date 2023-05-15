package com.samus.ontop.ontoptest.adapters.external;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.samus.ontop.ontoptest.application.domain.integration.WalletRequest;
import com.samus.ontop.ontoptest.application.domain.integration.WalletResponse;
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

import java.math.BigDecimal;

@TestWithResources
@ExtendWith(MockitoExtension.class)
public class WalletExternalAdapterTest {
    @Mock
    private WebClient webClientMock;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersMock;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriMock;
    @Mock
    private WebClient.ResponseSpec responseMock;
    @InjectMocks
    WalletExternalAdapter walletExternalAdapter;
    @GivenJsonResource("wallet-200.json")
    WalletResponse walletResponse;

    @Test
    public void testOperation() {
        String userId = "1";
        WalletRequest walletRequest = new WalletRequest(userId, new BigDecimal(100));
        Mockito.when(webClientMock.post()).thenReturn(requestBodyUriMock);
        Mockito.when(requestBodyUriMock.uri("/wallets/transactions")).thenReturn(requestBodyUriMock);
        Mockito.when(requestBodyUriMock.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriMock);
        Mockito.when(requestBodyUriMock.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriMock);
        Mockito.when(requestBodyUriMock.bodyValue(walletRequest)).thenReturn(requestHeadersMock);
        Mockito.when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        Mockito.when(responseMock.bodyToMono(WalletResponse.class)).thenReturn(Mono.just(walletResponse));
        StepVerifier.create(walletExternalAdapter.operation(walletRequest))
                .expectNextMatches(e -> e.getAmount().equals(walletResponse.getAmount()))
                .verifyComplete();
    }

    @Test
    public void testOperationFailed() {
        String userId = "1";
        WalletRequest walletRequest = new WalletRequest(userId, new BigDecimal(100));
        Mockito.when(webClientMock.post()).thenReturn(requestBodyUriMock);
        Mockito.when(requestBodyUriMock.uri("/wallets/transactions")).thenReturn(requestBodyUriMock);
        Mockito.when(requestBodyUriMock.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriMock);
        Mockito.when(requestBodyUriMock.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriMock);
        Mockito.when(requestBodyUriMock.bodyValue(walletRequest)).thenReturn(requestHeadersMock);
        Mockito.when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        Mockito.when(responseMock.bodyToMono(WalletResponse.class)).thenReturn(Mono.error(new RuntimeException("Mock error")));
        StepVerifier.create(walletExternalAdapter.operation(walletRequest))
                .expectError().verify();
    }
}

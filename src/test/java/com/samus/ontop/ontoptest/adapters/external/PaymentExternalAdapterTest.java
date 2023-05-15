package com.samus.ontop.ontoptest.adapters.external;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.samus.ontop.ontoptest.application.domain.integration.*;
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
public class PaymentExternalAdapterTest {
    @Mock
    private WebClient webClientMock;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersMock;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriMock;
    @Mock
    private WebClient.ResponseSpec responseMock;
    @InjectMocks
    PaymentExternalAdapter paymentExternalAdapter;
    @GivenJsonResource("payment-200.json")
    PaymentResponse paymentResponse;

    @Test
    public void testOperation() {
        Mockito.when(webClientMock.post()).thenReturn(requestBodyUriMock);
        Mockito.when(requestBodyUriMock.uri("/api/v1/payments")).thenReturn(requestBodyUriMock);
        Mockito.when(requestBodyUriMock.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriMock);
        Mockito.when(requestBodyUriMock.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriMock);
        Mockito.when(requestBodyUriMock.bodyValue(getPaymentRequest())).thenReturn(requestHeadersMock);
        Mockito.when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        Mockito.when(responseMock.bodyToMono(PaymentResponse.class)).thenReturn(Mono.just(paymentResponse));
        StepVerifier.create(paymentExternalAdapter.paymentTransfer(getPaymentRequest()))
                .expectNextMatches(e -> e.getPaymentInfo().getAmount().equals(paymentResponse.getPaymentInfo().getAmount()))
                .verifyComplete();
    }

    @Test
    public void testOperationFailed() {
        Mockito.when(webClientMock.post()).thenReturn(requestBodyUriMock);
        Mockito.when(requestBodyUriMock.uri("/api/v1/payments")).thenReturn(requestBodyUriMock);
        Mockito.when(requestBodyUriMock.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriMock);
        Mockito.when(requestBodyUriMock.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriMock);
        Mockito.when(requestBodyUriMock.bodyValue(getPaymentRequest())).thenReturn(requestHeadersMock);
        Mockito.when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        Mockito.when(responseMock.bodyToMono(PaymentResponse.class)).thenReturn(Mono.error(new RuntimeException("Error mock")));
        StepVerifier.create(paymentExternalAdapter.paymentTransfer(getPaymentRequest()))
                .expectError().verify();
    }

    private PaymentRequest getPaymentRequest() {
        SourceInformation sourceInformation = new SourceInformation("test");
        PaymentAccount paymentAccount = new PaymentAccount("22", "USD", "22");
        Source source = new Source("Test", sourceInformation, paymentAccount);
        Destination destination = new Destination("test", paymentAccount);
        return new PaymentRequest(source, destination, new BigDecimal(100));
    }
}

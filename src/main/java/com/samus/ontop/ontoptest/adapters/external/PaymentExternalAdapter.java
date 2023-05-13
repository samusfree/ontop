package com.samus.ontop.ontoptest.adapters.external;

import com.samus.ontop.ontoptest.application.domain.integration.PaymentRequest;
import com.samus.ontop.ontoptest.application.domain.integration.PaymentResponse;
import com.samus.ontop.ontoptest.application.ports.out.PaymentPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class PaymentExternalAdapter implements PaymentPort {
    @Autowired
    private WebClient webClient;

    @Override
    public Mono<PaymentResponse> paymentTransfer(PaymentRequest request) {
        return webClient.post().uri("/api/v1/payments").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request).retrieve().bodyToMono(PaymentResponse.class);
    }
}

package com.samus.ontop.ontoptest.application.ports.out;

import com.samus.ontop.ontoptest.application.domain.integration.PaymentRequest;
import com.samus.ontop.ontoptest.application.domain.integration.PaymentResponse;
import reactor.core.publisher.Mono;

public interface PaymentPort {
    Mono<PaymentResponse> paymentTransfer(PaymentRequest request);
}

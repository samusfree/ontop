package com.samus.ontop.ontoptest.application.domain.integration;

import lombok.Data;

@Data
public class PaymentResponse {
    private RequestInfo requestInfo;
    private PaymentInfo paymentInfo;
}

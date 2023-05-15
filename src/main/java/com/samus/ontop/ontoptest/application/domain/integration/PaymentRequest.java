package com.samus.ontop.ontoptest.application.domain.integration;

import java.math.BigDecimal;

public record PaymentRequest(Source source, Destination destination, BigDecimal amount) {
}

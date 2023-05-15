package com.samus.ontop.ontoptest.application.domain.integration;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentInfo {
    private BigDecimal amount;
    private String id;
}

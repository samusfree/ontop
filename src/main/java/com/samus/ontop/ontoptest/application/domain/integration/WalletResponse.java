package com.samus.ontop.ontoptest.application.domain.integration;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WalletResponse {
    private Integer walletTransactionId;
    private BigDecimal amount;
    Integer userId;
}
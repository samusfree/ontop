package com.samus.ontop.ontoptest.application.domain;

import java.math.BigDecimal;

public record TransactionRequest(String userId, BigDecimal amount, TransactionType type, String currency) {
}

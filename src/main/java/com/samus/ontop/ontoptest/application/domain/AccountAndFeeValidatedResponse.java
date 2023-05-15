package com.samus.ontop.ontoptest.application.domain;

import java.math.BigDecimal;

public record AccountAndFeeValidatedResponse(Account account, BigDecimal fee) {
}

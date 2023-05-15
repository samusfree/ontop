package com.samus.ontop.ontoptest.application.ports.out;

import com.samus.ontop.ontoptest.application.domain.integration.UserBalance;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface UserPort {
    Mono<UserBalance> getBalance(String userId);

    Mono<Boolean> validateBalance(String userId, BigDecimal amount);

    Mono<BigDecimal> getFeePercentage(BigDecimal amount);
}

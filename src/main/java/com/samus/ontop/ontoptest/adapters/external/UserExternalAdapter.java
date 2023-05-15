package com.samus.ontop.ontoptest.adapters.external;

import com.samus.ontop.ontoptest.application.domain.exception.AccountNotFoundException;
import com.samus.ontop.ontoptest.application.domain.exception.InsufficientBalanceException;
import com.samus.ontop.ontoptest.application.domain.integration.UserBalance;
import com.samus.ontop.ontoptest.application.ports.out.UserPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
@Slf4j
public class UserExternalAdapter implements UserPort {
    @Autowired
    private WebClient webClient;
    @Value("${application.ontop.percentage.fee}")
    private int feePercentage;

    @Override
    public Mono<UserBalance> getBalance(String userId) {
        return webClient.get().uri("/wallets/balance?user_id=" + userId)
                .accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(UserBalance.class);
    }

    @Override
    public Mono<Boolean> validateBalance(String userId, BigDecimal amount) {
        return getBalance(userId).doOnError(e -> {
            log.error(e.getMessage(), e);
            throw new AccountNotFoundException(userId);
        }).flatMap(e -> {
            if (e.getBalance().compareTo(amount.add(getFeePercentage(amount).block())) < 0) {
                throw new InsufficientBalanceException(userId, amount);
            }
            return Mono.just(true);
        });
    }

    @Override
    public Mono<BigDecimal> getFeePercentage(BigDecimal amount) {
        return Mono.just(amount.multiply(new BigDecimal(feePercentage / 100.00)));
    }
}

package com.samus.ontop.ontoptest.application.service;

import com.samus.ontop.ontoptest.application.domain.Account;
import com.samus.ontop.ontoptest.application.ports.out.AccountPort;
import com.samus.ontop.ontoptest.common.CommonObjects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    AccountPort accountPort;

    @InjectMocks
    private AccountService accountService;

    @Test
    public void saveTest() {
        Mono<Account> monoAccount =
                Mono.just(CommonObjects.getAccount());

        Mockito.when(accountPort.save(CommonObjects.getAccount())).thenReturn(monoAccount);

        StepVerifier.create(accountService.save(CommonObjects.getAccount()))
                .expectNext(CommonObjects.getAccount())
                .verifyComplete();

    }
}

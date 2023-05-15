package com.samus.ontop.ontoptest.application.service;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.adelean.inject.resources.junit.jupiter.WithJacksonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.samus.ontop.ontoptest.adapters.external.UserExternalAdapter;
import com.samus.ontop.ontoptest.application.domain.PageSupport;
import com.samus.ontop.ontoptest.application.domain.Transaction;
import com.samus.ontop.ontoptest.application.domain.TransactionRequest;
import com.samus.ontop.ontoptest.application.domain.TransactionStatus;
import com.samus.ontop.ontoptest.application.domain.exception.AccountNotFoundException;
import com.samus.ontop.ontoptest.application.domain.exception.InsufficientBalanceException;
import com.samus.ontop.ontoptest.application.domain.exception.PaymentErrorException;
import com.samus.ontop.ontoptest.application.domain.exception.SaveTransactionException;
import com.samus.ontop.ontoptest.application.domain.integration.PaymentResponse;
import com.samus.ontop.ontoptest.application.domain.integration.UserBalance;
import com.samus.ontop.ontoptest.application.domain.integration.WalletResponse;
import com.samus.ontop.ontoptest.application.ports.out.AccountPort;
import com.samus.ontop.ontoptest.application.ports.out.PaymentPort;
import com.samus.ontop.ontoptest.application.ports.out.TransactionPort;
import com.samus.ontop.ontoptest.application.ports.out.WalletPort;
import com.samus.ontop.ontoptest.common.CommonObjects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.sql.SQLException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@TestWithResources
@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    private AccountPort accountPort;
    @Mock
    private TransactionPort transactionPort;
    @Mock
    private UserExternalAdapter userPort;
    @Mock
    private WalletPort walletPort;
    @Mock
    private PaymentPort paymentPort;
    @InjectMocks
    private TransactionService transactionService;
    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    @GivenJsonResource("balance-ok.json")
    UserBalance userBalance;
    @GivenJsonResource("balance-low.json")
    UserBalance userBalanceLow;
    @GivenJsonResource("payment-200.json")
    PaymentResponse paymentResponse;
    @GivenJsonResource("payment-500.json")
    PaymentResponse paymentFailedResponse;
    @GivenJsonResource("wallet-200.json")
    WalletResponse walletResponse;

    @Test
    public void listTest() {
        int page = 1, size = 1;
        String userId = "1";

        PageSupport<Transaction> pageSupport = new PageSupport<>(List.of(CommonObjects.getTransaction(false,
                123, "123", TransactionStatus.CREATED)), page, size, size);

        Mockito.when(transactionPort.list(userId, page, size)).thenReturn(Mono.just(pageSupport));

        StepVerifier.create(transactionService.list(userId, page, size))
                .expectNext(pageSupport)
                .verifyComplete();
    }

    @Test
    public void testListError() {
        int page = 1, size = 1;
        String userId = "1";

        Mockito.when(transactionPort.list(userId, page, size)).thenReturn(Mono.error(new SQLException("Save error")));

        StepVerifier.create(transactionService.list(userId, page, size))
                .expectError().verify();
    }

    @Test
    public void testWithdrawComplete() {
        String userId = "1";
        TransactionRequest transactionRequest = CommonObjects.getTransactionRequest();
        Transaction transaction = CommonObjects.getTransaction(true, 67948,
                "04b5d7b6-112e-490b-9900-753ea1bd94ae", TransactionStatus.IN_PROGRESS);
        Transaction transactionExpected = CommonObjects.getTransaction(true, 67948,
                "04b5d7b6-112e-490b-9900-753ea1bd94ae", TransactionStatus.IN_PROGRESS);

        Mockito.when(userPort.validateBalance(userId, transactionRequest.amount())).thenCallRealMethod();
        Mockito.when(userPort.getBalance(userId)).thenReturn(Mono.just(userBalance));
        Mockito.when(userPort.getFeePercentage(transactionRequest.amount())).thenCallRealMethod();
        Mockito.when(accountPort.findByUserId(userId)).thenReturn(Mono.just(transaction.getAccount()));
        Mockito.when(transactionPort.save(any())).thenReturn(Mono.just(transaction))
                .thenReturn(Mono.just(transaction)).thenReturn(Mono.just(transaction));
        Mockito.when(paymentPort.paymentTransfer(any())).thenReturn(Mono.just(paymentResponse));
        Mockito.when(transactionPort.getById(any())).thenReturn(Mono.just(transaction)).thenReturn(Mono.just(transaction));
        Mockito.when(walletPort.operation(any())).thenReturn(Mono.just(walletResponse));

        StepVerifier.create(transactionService.withdraw(transactionRequest))
                .expectNextMatches(e -> e.toString().equals(transactionExpected.toString())).verifyComplete();
    }

    @Test
    public void testWithdrawInsuficientBalance() {
        String userId = "1";
        TransactionRequest transactionRequest = CommonObjects.getTransactionRequest();

        Mockito.when(userPort.validateBalance(userId, transactionRequest.amount())).thenCallRealMethod();
        Mockito.when(userPort.getBalance(userId)).thenReturn(Mono.just(userBalanceLow));
        Mockito.when(userPort.getFeePercentage(transactionRequest.amount())).thenCallRealMethod();
        Mockito.when(accountPort.findByUserId(userId)).thenReturn(Mono.just(CommonObjects.getAccount()));

        StepVerifier.create(transactionService.withdraw(transactionRequest))
                .expectErrorMatches(e -> e instanceof InsufficientBalanceException).verify();
    }

    @Test
    public void testWithdrawErrorToGetBalance() {
        String userId = "1";
        TransactionRequest transactionRequest = CommonObjects.getTransactionRequest();

        Mockito.when(userPort.validateBalance(userId, transactionRequest.amount())).thenCallRealMethod();
        Mockito.when(userPort.getBalance(userId)).thenReturn(Mono.error(new RuntimeException("Mock error")));
        Mockito.when(userPort.getFeePercentage(transactionRequest.amount())).thenCallRealMethod();
        Mockito.when(accountPort.findByUserId(userId)).thenReturn(Mono.just(CommonObjects.getAccount()));

        StepVerifier.create(transactionService.withdraw(transactionRequest))
                .expectErrorMatches(e -> e instanceof AccountNotFoundException).verify();
    }

    @Test
    public void testWithdrawErrorToSaveCreatedTransaction() {
        String userId = "1";
        TransactionRequest transactionRequest = CommonObjects.getTransactionRequest();

        Mockito.when(userPort.validateBalance(userId, transactionRequest.amount())).thenCallRealMethod();
        Mockito.when(userPort.getBalance(userId)).thenReturn(Mono.just(userBalance));
        Mockito.when(userPort.getFeePercentage(transactionRequest.amount())).thenCallRealMethod();
        Mockito.when(accountPort.findByUserId(userId)).thenReturn(Mono.just(CommonObjects.getAccount()));
        Mockito.when(transactionPort.save(any())).thenReturn(Mono.error(new SQLException("Save error")));

        StepVerifier.create(transactionService.withdraw(transactionRequest))
                .expectErrorMatches(e -> e instanceof SaveTransactionException).verify();
    }

    @Test
    public void testWithdrawErrorToDoPaymenntOperation() {
        String userId = "1";
        TransactionRequest transactionRequest = CommonObjects.getTransactionRequest();
        Transaction transaction = CommonObjects.getTransaction(true, null,
                null, TransactionStatus.FAILED);

        Mockito.when(userPort.validateBalance(userId, transactionRequest.amount())).thenCallRealMethod();
        Mockito.when(userPort.getBalance(userId)).thenReturn(Mono.just(userBalance));
        Mockito.when(userPort.getFeePercentage(transactionRequest.amount())).thenCallRealMethod();
        Mockito.when(accountPort.findByUserId(userId)).thenReturn(Mono.just(CommonObjects.getAccount()));
        Mockito.when(transactionPort.save(any())).thenReturn(Mono.just(transaction))
                .thenReturn(Mono.just(transaction));
        Mockito.when(paymentPort.paymentTransfer(any())).thenReturn(Mono.error(new RuntimeException("Mock error")));
        Mockito.when(transactionPort.getById(any())).thenReturn(Mono.just(transaction));

        StepVerifier.create(transactionService.withdraw(transactionRequest))
                .expectErrorMatches(e -> e instanceof PaymentErrorException).verify();
    }

    @Test
    public void testWithdrawErrorToDoPaymenntOperationReturnFailed() {
        String userId = "1";
        TransactionRequest transactionRequest = CommonObjects.getTransactionRequest();
        Transaction transaction = CommonObjects.getTransaction(true, null,
                "04b5d7b6-112e-490b-9900-753ea1bd94ae", TransactionStatus.FAILED);

        Mockito.when(userPort.validateBalance(userId, transactionRequest.amount())).thenCallRealMethod();
        Mockito.when(userPort.getBalance(userId)).thenReturn(Mono.just(userBalance));
        Mockito.when(userPort.getFeePercentage(transactionRequest.amount())).thenCallRealMethod();
        Mockito.when(accountPort.findByUserId(userId)).thenReturn(Mono.just(CommonObjects.getAccount()));
        Mockito.when(transactionPort.save(any())).thenReturn(Mono.just(transaction))
                .thenReturn(Mono.just(transaction)).thenReturn(Mono.just(transaction));
        Mockito.when(paymentPort.paymentTransfer(any())).thenReturn(Mono.just(paymentFailedResponse));
        Mockito.when(transactionPort.getById(any())).thenReturn(Mono.just(transaction)).thenReturn(Mono.just(transaction));

        StepVerifier.create(transactionService.withdraw(transactionRequest))
                .expectErrorMatches(e -> e instanceof PaymentErrorException).verify();
    }

    @Test
    public void testWithdrawErrorToDoWalletOperation() {
        String userId = "1";
        TransactionRequest transactionRequest = CommonObjects.getTransactionRequest();
        Transaction transaction = CommonObjects.getTransaction(true, null,
                "04b5d7b6-112e-490b-9900-753ea1bd94ae", TransactionStatus.IN_PROGRESS);
        Transaction transactionExpected = CommonObjects.getTransaction(true, null,
                "04b5d7b6-112e-490b-9900-753ea1bd94ae", TransactionStatus.IN_PROGRESS);

        Mockito.when(userPort.validateBalance(userId, transactionRequest.amount())).thenCallRealMethod();
        Mockito.when(userPort.getBalance(userId)).thenReturn(Mono.just(userBalance));
        Mockito.when(userPort.getFeePercentage(transactionRequest.amount())).thenCallRealMethod();
        Mockito.when(accountPort.findByUserId(userId)).thenReturn(Mono.just(CommonObjects.getAccount()));
        Mockito.when(transactionPort.save(any())).thenReturn(Mono.just(transaction))
                .thenReturn(Mono.just(transaction)).thenReturn(Mono.just(transaction));
        Mockito.when(paymentPort.paymentTransfer(any())).thenReturn(Mono.just(paymentResponse));
        Mockito.when(transactionPort.getById(any())).thenReturn(Mono.just(transaction)).thenReturn(Mono.just(transaction))
                .thenReturn(Mono.just(transaction)).thenReturn(Mono.just(transaction));
        Mockito.when(walletPort.operation(any())).thenReturn(Mono.error(new RuntimeException("Mock error")));

        StepVerifier.create(transactionService.withdraw(transactionRequest))
                .expectNextMatches(e -> e.toString().equals(transactionExpected.toString())).verifyComplete();
    }
}

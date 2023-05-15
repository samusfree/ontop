package com.samus.ontop.ontoptest.application.service;

import com.samus.ontop.ontoptest.application.domain.*;
import com.samus.ontop.ontoptest.application.domain.exception.OntopException;
import com.samus.ontop.ontoptest.application.domain.exception.PaymentErrorException;
import com.samus.ontop.ontoptest.application.domain.exception.SaveTransactionException;
import com.samus.ontop.ontoptest.application.domain.integration.Source;
import com.samus.ontop.ontoptest.application.domain.integration.WalletRequest;
import com.samus.ontop.ontoptest.application.domain.integration.WalletResponse;
import com.samus.ontop.ontoptest.application.ports.in.TransactionManagementUseCase;
import com.samus.ontop.ontoptest.application.ports.out.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Service
@Slf4j
public class TransactionService implements TransactionManagementUseCase {
    private final AccountPort accountPort;
    private final TransactionPort transactionPort;
    private final UserPort userPort;
    private final WalletPort walletPort;
    private final PaymentPort paymentPort;
    private final Source ontopSource;

    @Autowired
    public TransactionService(AccountPort accountPort, TransactionPort transactionPort, UserPort userPort, WalletPort walletPort, PaymentPort paymentPort, Source ontopSource) {
        this.accountPort = accountPort;
        this.transactionPort = transactionPort;
        this.userPort = userPort;
        this.walletPort = walletPort;
        this.paymentPort = paymentPort;
        this.ontopSource = ontopSource;
    }

    @Override
    public Mono<Transaction> withdraw(TransactionRequest request) {
        return preValidateTransaction(request).flatMap(accountAndFeeValidatedResponse -> {
            Account account = accountAndFeeValidatedResponse.account();
            BigDecimal fee = accountAndFeeValidatedResponse.fee();
            return transactionPort.save(TransactionRequestMapper.toNewTransaction(request, account, fee)).doOnError(e -> {
                log.error(e.getMessage(), e);
                throw new SaveTransactionException(request.userId());
            }).flatMap(transaction ->
                    paymentPort.paymentTransfer(
                            TransactionRequestMapper.toPaymentRequest(account, request, transaction.getAmount(),
                                    ontopSource)).onErrorResume(e -> {
                        log.error(e.getMessage(), e);
                        return transactionPort.getById(transaction.getId()).flatMap(transactionToUpdate -> {
                            transactionToUpdate.setStatus(TransactionStatus.FAILED);
                            return transactionPort.save(transactionToUpdate)
                                    .doOnNext(data -> log.info("Transaction failed updated:  " + data))
                                    .then(Mono.error(new PaymentErrorException(request.userId())));
                        });
                    }).flatMap(paymentResponse -> {
                        TransactionStatus status = paymentResponse.getRequestInfo().getStatus()
                                .equals(Constants.PAYMENT_STATUS_FAILED) ?
                                TransactionStatus.FAILED : TransactionStatus.IN_PROGRESS;
                        return transactionPort.getById(transaction.getId()).flatMap(transactionToUpdate -> {
                            transactionToUpdate.setStatus(status);
                            transactionToUpdate.setPaymentInfoId(paymentResponse.getPaymentInfo().getId());
                            transactionToUpdate.setUpdateDate(LocalDate.now());
                            return transactionPort.save(transactionToUpdate).flatMap(
                                    savedTransaction -> {
                                        if (savedTransaction.getStatus() == TransactionStatus.FAILED) {
                                            throw new PaymentErrorException(request.userId());
                                        }
                                        return walletPort.operation(new WalletRequest(request.userId(), transaction.getAmount()))
                                                .doOnError(e -> log.error("Error to send the wallet operation: " + e.getMessage(), e))
                                                .onErrorResume(e -> Mono.just(new WalletResponse()))
                                                .flatMap(walletResponse -> transactionPort.getById(transaction.getId()).flatMap(transactionToUpdateFinal -> {
                                                    if (Objects.isNull(walletResponse.getWalletTransactionId())) {
                                                        return Mono.just(transactionToUpdateFinal);
                                                    }
                                                    transactionToUpdateFinal.setStatus(status);
                                                    transactionToUpdateFinal.setWalletTransactionId(walletResponse.getWalletTransactionId());
                                                    transactionToUpdateFinal.setUpdateDate(LocalDate.now());
                                                    return transactionPort.save(transactionToUpdateFinal);
                                                }));
                                    }
                            );
                        });
                    })
            );
        });
    }

    @Override
    public Mono<PageSupport<Transaction>> list(String userId, int page, int size) {
        return transactionPort.list(userId, page, size).doOnError(e -> {
            log.error(e.getMessage(), e);
            throw new OntopException("Problems to obtain the list of transactions: " + e.getMessage());
        });
    }

    private Mono<AccountAndFeeValidatedResponse> preValidateTransaction(TransactionRequest request) {
        return Mono.zip(userPort.validateBalance(request.userId(), request.amount()),
                        accountPort.findByUserId(request.userId()), userPort.getFeePercentage(request.amount()))
                .flatMap(e -> Mono.just(new AccountAndFeeValidatedResponse(e.getT2(), e.getT3())));
    }
}

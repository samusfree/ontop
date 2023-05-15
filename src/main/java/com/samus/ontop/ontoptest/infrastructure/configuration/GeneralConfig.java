package com.samus.ontop.ontoptest.infrastructure.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.samus.ontop.ontoptest.application.domain.ErrorResponse;
import com.samus.ontop.ontoptest.application.domain.exception.*;
import com.samus.ontop.ontoptest.application.domain.integration.PaymentAccount;
import com.samus.ontop.ontoptest.application.domain.integration.Source;
import com.samus.ontop.ontoptest.application.domain.integration.SourceInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.Map;

@Configuration
public class GeneralConfig {
    @Value("${application.ontop.source.type}")
    private String sourceType;
    @Value("${application.ontop.source.name}")
    private String sourceName;
    @Value("${application.ontop.source.accountNumber}")
    private String sourceAccountNumber;
    @Value("${application.ontop.source.routingNumber}")
    private String sourceRoutingNumber;
    @Value("${application.ontop.source.currency}")
    private String sourceCurrency;
    @Value("${application.ontop.external.services.url}")
    private String externalServiceURL;
    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public Source ontopSource() {
        return new Source(sourceType, new SourceInformation(sourceName), new PaymentAccount(sourceAccountNumber, sourceCurrency, sourceRoutingNumber));
    }

    @Bean
    public WebClient webClient() {
        return WebClient.create(externalServiceURL);
    }

    @Bean
    @Order(-2)
    public WebExceptionHandler exceptionHandler() {
        return (ServerWebExchange exchange, Throwable ex) -> {
            DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
            if (ex instanceof OntopException) {
                exchange.getResponse().setStatusCode(exceptionToStatusCode().getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR));
                exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json ");
                DataBuffer dataBuffer;
                try {
                    dataBuffer = bufferFactory.wrap(objectMapper.writeValueAsBytes(
                            new ErrorResponse(exceptionToStatusCode()
                                    .getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR).value(), ex.getMessage())));
                } catch (JsonProcessingException e) {
                    dataBuffer = bufferFactory.wrap("".getBytes());
                }

                return exchange.getResponse().writeWith(Mono.just(dataBuffer));
            }
            return Mono.error(ex);
        };
    }

    @Bean
    public Map<Class<? extends RuntimeException>, HttpStatus> exceptionToStatusCode() {
        return Map.of(
                AccountNotFoundException.class, HttpStatus.NOT_FOUND,
                BadRequestException.class, HttpStatus.BAD_REQUEST,
                InsufficientBalanceException.class, HttpStatus.BAD_REQUEST,
                OntopException.class, HttpStatus.INTERNAL_SERVER_ERROR,
                PaymentErrorException.class, HttpStatus.INTERNAL_SERVER_ERROR,
                SaveTransactionException.class, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

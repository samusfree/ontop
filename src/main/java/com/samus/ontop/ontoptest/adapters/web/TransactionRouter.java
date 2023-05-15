package com.samus.ontop.ontoptest.adapters.web;

import com.samus.ontop.ontoptest.application.domain.ErrorResponse;
import com.samus.ontop.ontoptest.application.domain.PageSupport;
import com.samus.ontop.ontoptest.application.domain.Transaction;
import com.samus.ontop.ontoptest.application.domain.TransactionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class TransactionRouter {

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(path = "/transactions/withdraw", produces = {
                            MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.POST, beanClass = TransactionHandler.class, beanMethod = "withDraw",
                            operation = @Operation(operationId = "withDraw", responses = {
                                    @ApiResponse(responseCode = "201", description = "successful operation", content = @Content(schema = @Schema(implementation = Transaction.class))),
                                    @ApiResponse(responseCode = "400", description = "BadRequest", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))},
                                    requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = TransactionRequest.class))))
                    ),
                    @RouterOperation(path = "/transactions/list", produces = {
                            MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET, beanClass = TransactionHandler.class, beanMethod = "list",
                            operation = @Operation(operationId = "list", responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = PageSupportTransaction.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid attributes", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500", description = "Server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))},
                                    parameters = {
                                            @Parameter(in = ParameterIn.QUERY, name = "userId"),
                                            @Parameter(in = ParameterIn.QUERY, name = "page"),
                                            @Parameter(in = ParameterIn.QUERY, name = "size")
                                    })
                    )
            }
    )
    public RouterFunction<ServerResponse> transactionRoutes(TransactionHandler handler) {
        return route().path(
                "/transactions", builder -> builder
                        .POST("/withdraw", handler::withDraw)
                        .GET("/list", handler::list)
        ).build();
    }
}

class PageSupportTransaction extends PageSupport<Transaction> {
}

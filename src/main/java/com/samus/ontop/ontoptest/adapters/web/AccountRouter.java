package com.samus.ontop.ontoptest.adapters.web;

import com.samus.ontop.ontoptest.application.domain.Account;
import com.samus.ontop.ontoptest.application.domain.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
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
public class AccountRouter {
    @Bean
    @RouterOperations(
            {
                    @RouterOperation(path = "/accounts", produces = {
                            MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.POST, beanClass = AccountHandler.class, beanMethod = "save",
                            operation = @Operation(operationId = "save", responses = {
                                    @ApiResponse(responseCode = "201", description = "successful operation", content = @Content(schema = @Schema(implementation = Account.class))),
                                    @ApiResponse(responseCode = "400", description = "BadRequest", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500", description = "Error in save", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))},
                                    requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = Account.class))))
                    )
            }
    )
    public RouterFunction<ServerResponse> accountRoutes(AccountHandler handler) {
        return route().path(
                "/accounts", builder -> builder
                        .POST("", handler::save)
        ).build();
    }
}

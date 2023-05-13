package com.samus.ontop.ontoptest;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories
@OpenAPIDefinition(info = @Info(title = "Ontop challenge", version = "1.0", description = "Documentation APIs v1.0"))
public class OntoptestApplication {
    public static void main(String[] args) {
        SpringApplication.run(OntoptestApplication.class, args);
    }

}

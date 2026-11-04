package com.serviceflow.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI serviceFlowOpenApi() {
        String basicSchemeName = "basicAuth";
        String bearerSchemeName = "bearerAuth";

        // Two separate requirements mean either scheme is accepted.
        return new OpenAPI()
                .info(new Info()
                        .title("ServiceFlow API")
                        .version("0.0.1")
                        .description("Mini CRM and invoicing API for small service businesses"))
                .addSecurityItem(new SecurityRequirement().addList(basicSchemeName))
                .addSecurityItem(new SecurityRequirement().addList(bearerSchemeName))
                .components(new Components()
                        .addSecuritySchemes(basicSchemeName, new SecurityScheme()
                                .name(basicSchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic"))
                        .addSecuritySchemes(bearerSchemeName, new SecurityScheme()
                                .name(bearerSchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}

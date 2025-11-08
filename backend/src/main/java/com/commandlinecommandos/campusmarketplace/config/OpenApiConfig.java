package com.commandlinecommandos.campusmarketplace.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation
 * Access at: http://localhost:8080/api/swagger-ui/index.html
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI campusMarketplaceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Campus Marketplace API")
                .description("REST API for Campus Marketplace User Management System")
                .version("v1.0")
                .contact(new Contact()
                    .name("Command Line Commando Team")
                    .email("support@campusmarketplace.edu"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080/api")
                    .description("Development Server"),
                new Server()
                    .url("https://api.campusmarketplace.edu/api")
                    .description("Production Server")))
            .components(new Components()
                .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .in(SecurityScheme.In.HEADER)
                    .name("Authorization")))
            .addSecurityItem(new SecurityRequirement()
                .addList("bearer-jwt"));
    }
}


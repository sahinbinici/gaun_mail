package com.mobilewebservices.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        // Use relative URL so it works on any server
        String serverUrl = contextPath.equals("/") ? "/" : contextPath;

        return new OpenAPI()
                .info(new Info()
                        .title("Food Announcements News Event Services API")
                        .description("Comprehensive API for managing food programs, announcements, news, and events")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url(serverUrl)
                                .description("Current server")
                ))
                // Security disabled - no authentication required for now
                // .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                // .components(new Components()
                //         .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
                ;
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter JWT token obtained from /auth/login endpoint");
    }
}

package com.personneltrackingsystem.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
public class OpenApiConfig {

    @Bean
    public OpenAPI baseOpenAPI(@Value("${application-description}") String description,
                                 @Value("${application-version}") String version){

                        
        final String BEARER_AUTH = "bearerAuth";
        final String BASIC_AUTH = "basicAuth";
        final String OAUTH2_AUTH = "oauth2";   


        return new OpenAPI()
                .info(new Info()
                        .title("PTS OpenAPI Swagger")
                        .version(version)
                        .description(description)
                        .license(new License().name("PTS API License")))

                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"))
                        .addSecuritySchemes(BASIC_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic"))
                        .addSecuritySchemes(OAUTH2_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl("https://auth-server.com/oauth/authorize")
                                                .tokenUrl("https://auth-server.com/oauth/token")
                                                .scopes(new Scopes()
                                                        .addString("read", "Permission to read")
                                                        .addString("write", "Permission to write")
                                                )
                                        )
                                )
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .addSecurityItem(new SecurityRequirement().addList(BASIC_AUTH))
                .addSecurityItem(new SecurityRequirement().addList(OAUTH2_AUTH, List.of("read", "write")));


    }
}

package com.myme.mywarehome.infrastructure.config.swagger;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("MyWareHome Sagger API")
                .version("v1.0")
                .description("MyWareHome API 문서입니다.");

        // Security 스키마 설정
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        // Security 요청 설정
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        // Tags 순서 설정
        List<Tag> tags = Arrays.asList(
                new Tag().name("Auth").description("인증 관련 API")
        );



        return new OpenAPI()
                .addServersItem(new Server().url("https://mywareho.me").description("Production"))
                .addServersItem(new Server().url("http://localhost:8080").description("Local"))
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .tags(tags);
    }




}

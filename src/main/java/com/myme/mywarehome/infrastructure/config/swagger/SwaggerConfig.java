package com.myme.mywarehome.infrastructure.config.swagger;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${app.base-url}")
    private String appBaseUrl;

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("MyWareHome API Specification")
                .version("v1.0")
                .description("MyWareHome API 문서입니다.");

        // Tags 순서 설정
        List<Tag> tags = Arrays.asList(
                new Tag().name("Auth").description("인증 관련 API")
        );

        return new OpenAPI()
                .addServersItem(new Server().url(appBaseUrl).description("Production"))
                .info(info)
                .tags(tags);
    }

}

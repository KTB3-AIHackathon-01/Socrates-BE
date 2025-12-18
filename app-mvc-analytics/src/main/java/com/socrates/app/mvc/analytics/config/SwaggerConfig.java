package com.socrates.app.mvc.analytics.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    @Bean
    public GroupedOpenApi analyticsApi() {
        return GroupedOpenApi.builder()
                .group("analytics-api")
                .pathsToMatch("/api/analytics/**")
                .build();
    }

    private Info apiInfo() {
        return new Info()
                .title("Analytics API") // API의 제목
                .description("Socrates Analytics API Documentation") // API에 대한 설명
                .version("1.0.0"); // API의 버전
    }
}

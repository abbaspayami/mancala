package com.bol.mancala.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * config Swagger
 *
 * @author Abbas
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(regex("/mancala.*"))
                .build()
                .apiInfo(metaData());
    }

    /**
     * config meta data swagger
     * @return metaData info
     */
    private ApiInfo metaData() {
        return new ApiInfo(
                "Mancala REST API",
                "Mancala game REST API for Bol.com",
                "1.0",
                "Mancala of service",
                new Contact("Abbas Payami", "", "payami2013@gmail.com"),
                "",
                "");
    }
}

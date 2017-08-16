package com.benayn.constell.service.server.config;

import static springfox.documentation.builders.PathSelectors.any;
import static springfox.documentation.builders.RequestHandlerSelectors.withClassAnnotation;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public Docket capricornApi() {
        return new Docket(SWAGGER_2)
            .select().apis(withClassAnnotation(RestController.class))
            .paths(any())
            .build()
            .useDefaultResponseMessages(false)
            ;
    }

}

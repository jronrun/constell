package com.benayn.constell.service.server.config;

import static com.benayn.constell.service.common.BaseConstants.PRODUCT_ENVIROMENT;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.of;
import static springfox.documentation.builders.PathSelectors.any;
import static springfox.documentation.builders.RequestHandlerSelectors.none;
import static springfox.documentation.builders.RequestHandlerSelectors.withClassAnnotation;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    /**
     *
     */
    private Environment environment;

    @Bean
    public Docket capricornApi() {
        return isProductEnviroment()
            ? new Docket(SWAGGER_2).select().apis(none()).build()
            : new Docket(SWAGGER_2)
                .select().apis(withClassAnnotation(RestController.class))
                .paths(any())
                .build()
                .useDefaultResponseMessages(false)
            ;
    }

    private boolean isProductEnviroment() {
        return newArrayList(of(environment.getActiveProfiles()).orElse(new String[]{})).contains(PRODUCT_ENVIROMENT);
    }

    @Autowired
    public SwaggerConfiguration(Environment environment) {
        this.environment = environment;
    }

}

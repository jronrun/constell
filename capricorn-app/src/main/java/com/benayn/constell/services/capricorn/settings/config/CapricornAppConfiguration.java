package com.benayn.constell.services.capricorn.settings.config;

import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.SERVICE_NAME;

import com.benayn.constell.service.server.annotation.EnableBenaynSwagger;
import com.benayn.constell.service.server.service.BenaynServiceInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;

@Configuration
@EnableBenaynSwagger
public class CapricornAppConfiguration {

    @Bean
    public BenaynServiceInfo benaynServiceInfo() {
        return BenaynServiceInfo.of(new ApiInfoBuilder().title("Capricorn API Document").build(), SERVICE_NAME);
    }

}

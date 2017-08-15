package com.benayn.constell.services.capricorn.config;

import com.benayn.constell.service.server.repository.ExamplePageFeature;
import com.benayn.constell.service.server.repository.bean.MybatisExamplePageFeature;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.benayn.constell.services.capricorn.mapper")
public class MybatisConfiguration {

    @Bean
    public ExamplePageFeature examplePageFeature() {
        return new MybatisExamplePageFeature();
    }

}

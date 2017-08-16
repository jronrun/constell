package com.benayn.constell.service.server.config;

import static springfox.documentation.builders.PathSelectors.any;
import static springfox.documentation.builders.RequestHandlerSelectors.withClassAnnotation;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

import com.benayn.constell.service.server.service.BenaynServiceInfo;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfiguration implements ApplicationContextAware {

    /**
     *
     */
    @Setter
    private BenaynServiceInfo benaynServiceInfo;

    @Bean
    public Docket capricornApi() {
        Docket docket = new Docket(SWAGGER_2)
            .groupName("constellation")
            .select()
            .apis(withClassAnnotation(RestController.class))
            .paths(any())
            .build();

        if (null != benaynServiceInfo) {
            docket.apiInfo(benaynServiceInfo.getApiInfo());
        }

        return docket;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext.containsBean("benaynServiceInfo")) {
            setBenaynServiceInfo(applicationContext.getBean(BenaynServiceInfo.class));
        }
    }
}

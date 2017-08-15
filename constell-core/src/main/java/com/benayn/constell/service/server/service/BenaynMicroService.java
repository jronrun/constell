package com.benayn.constell.service.server.service;

import com.benayn.constell.service.server.check.BenaynServiceRegister;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {
    "com.benayn.constell.service.server.filter",
    "com.benayn.constell.service.server.aspect",
    "com.benayn.constell.service.server.endpoint",
    "com.benayn.constell.service.server.log",
    "com.benayn.constell.service.server.health",
    "com.benayn.constell.service.server.config",
    "com.benayn.constell.service.server.security",
    "com.benayn.constell.service.metrics",
    "com.benayn.constell.service.server.service",
    "com.benayn.constell.service.server.annotation.processor",
    "com.benayn.constell.services" })
@Slf4j
@EnableBenaynService
public class BenaynMicroService {

    public BenaynMicroService() {

    }

    public ApplicationContext start(Class<?> primarySource, String[] args) {

        SpringApplication springApplication = new SpringApplication(primarySource);
        springApplication.setBannerMode(Banner.Mode.CONSOLE);

        springApplication.addListeners(new BenaynServiceRegister());
        ApplicationContext ctx = springApplication.run(args);

        if (log.isDebugEnabled()) {
            String[] beanNames = ctx.getBeanDefinitionNames();
            log.debug("The beans provided by Spring Boot， total {} beans:", beanNames.length);

            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                log.debug(beanName);
            }
        }

        return ctx;
    }

}

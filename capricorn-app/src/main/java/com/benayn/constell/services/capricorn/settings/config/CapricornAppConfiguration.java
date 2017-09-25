package com.benayn.constell.services.capricorn.settings.config;

import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.SERVICE_NAME;

import com.benayn.constell.service.server.annotation.EnableBenaynSwagger;
import com.benayn.constell.service.server.service.BenaynServiceInfo;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import springfox.documentation.builders.ApiInfoBuilder;

@Configuration
@EnableBenaynSwagger
public class CapricornAppConfiguration {

    @Bean
    public BenaynServiceInfo benaynServiceInfo() {
        return BenaynServiceInfo.of(new ApiInfoBuilder().title("Capricorn API Document").build(), SERVICE_NAME);
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        return new LocaleChangeInterceptor();
    }

    @Bean(name="localeResolver")
    public LocaleResolver localeResolverBean() {
        return new CookieLocaleResolver();
    }

    @Configuration
    public class CapricornWebMvcConfigurer implements WebMvcConfigurer {

        private CapricornConfigurer capricornConfigurer;

        @Autowired
        public CapricornWebMvcConfigurer(CapricornConfigurer capricornConfigurer) {
            this.capricornConfigurer = capricornConfigurer;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(localeChangeInterceptor());
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            capricornConfigurer.getResources()
                .forEach(resource -> {
                    registry
                        .addResourceHandler(resource.getHandler())
                        .addResourceLocations(resource.getLocation());
                }
            );
        }

    }

    /*
    capricorn:
      configurer:
        resources:
        -
         handler: /test1/**
         location: classpath:/static/components/golgi/
        -
         handler: /test2/**
         location: file:///Users/test/Documents/
     */

    @Data
    @NoArgsConstructor
    @Configuration
    @ConfigurationProperties(prefix = "capricorn.configurer")
    public static class CapricornConfigurer {

        private List<StaticResourcesConfigurer> resources = Lists.newArrayList();

        @Data
        @NoArgsConstructor
        static class StaticResourcesConfigurer {
            private String handler;
            private String location;
        }
    }

}

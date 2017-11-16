package com.benayn.constell.services.capricorn.settings.config;

import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.SERVICE_NAME;

import com.benayn.constell.service.server.annotation.EnableBenaynSwagger;
import com.benayn.constell.service.server.component.ViewObjectResolver;
import com.benayn.constell.service.server.component.ViewObjectResolverBean;
import com.benayn.constell.service.server.dialect.StarsDialect;
import com.benayn.constell.service.server.filter.CookieCredentialsFilter;
import com.benayn.constell.service.server.service.BenaynServiceInfo;
import com.benayn.constell.services.capricorn.settings.interceptor.PJAXInterceptor;
import com.benayn.constell.services.capricorn.settings.security.ConstellationLogoutHandler;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import springfox.documentation.builders.ApiInfoBuilder;

@Configuration
@EnableBenaynSwagger
public class CapricornAppConfiguration {

    @Bean
    public BenaynServiceInfo benaynServiceInfo() {
        return BenaynServiceInfo.of(new ApiInfoBuilder().title("Capricorn API Document").build(), SERVICE_NAME);
    }

    @Bean
    public ViewObjectResolver viewObjectResolver(MessageSource messageSource) {
        return new ViewObjectResolverBean(messageSource, fragmentTemplateEngine(messageSource));
    }

    @Bean
    public FilterRegistrationBean cookieCredentialsFilter() {
        FilterRegistrationBean<CookieCredentialsFilter> credentialsFilter = new FilterRegistrationBean<>();
        credentialsFilter.setOrder(Ordered.HIGHEST_PRECEDENCE);
        credentialsFilter.addUrlPatterns("/*");
        credentialsFilter.setFilter(new CookieCredentialsFilter());
        return credentialsFilter;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        return new LocaleChangeInterceptor();
    }

    @Bean(name = "localeResolver")
    public LocaleResolver localeResolverBean() {
        return new CookieLocaleResolver();
    }

    @Bean
    public StarsDialect starsDialect() {
        return new StarsDialect();
    }

    @Bean
    public LogoutHandler constellationLogoutHandler() {
        return new ConstellationLogoutHandler();
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
            registry.addInterceptor(new PJAXInterceptor());
            registry.addInterceptor(localeChangeInterceptor());
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            capricornConfigurer.getResources()
                .forEach(resource -> registry
                    .addResourceHandler(resource.getHandler())
                    .addResourceLocations(resource.getLocation())
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

        private String clientId;
        private String clientSecret;
        private List<StaticResourcesConfigurer> resources = Lists.newArrayList();

        @Data
        @NoArgsConstructor
        static class StaticResourcesConfigurer {

            private String handler;
            private String location;
        }
    }

    /*
        @Autowired
        private TemplateEngine textTemplateEngine;
        ...

        Context context = new Context();
        context.setVariable("name", "test name");
        context.setVariable("tags", "#spring #framework #java".split(" "));
        String text = textTemplateEngine.process("test", context);
        ...

        test.txt
        Name: [(${name})]
        Tags:
        [# th:each="tag : ${tags}" ]
            <div id="[(${tag})]">[(${tag})]</div>
        [/]

        @Bean(name = "textTemplateEngine")
        public TemplateEngine textTemplateEngine() {
            TemplateEngine templateEngine = new TemplateEngine();
            templateEngine.addTemplateResolver(textTemplateResolver());
            return templateEngine;
        }

        private ITemplateResolver textTemplateResolver() {
            ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
            templateResolver.setPrefix("/templates/manage/fragment/");
            templateResolver.setSuffix(".txt");
            //https://github.com/thymeleaf/thymeleaf/issues/395
            templateResolver.setTemplateMode(TemplateMode.TEXT);
            templateResolver.setCharacterEncoding("UTF8");
            templateResolver.setCheckExistence(true);
            templateResolver.setCacheable(false);
            return templateResolver;
        }
     */
    @Bean(name = "fragmentTemplateEngine")
    public TemplateEngine fragmentTemplateEngine(MessageSource messageSource) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(fragmentTemplateResolver());
        templateEngine.setMessageSource(messageSource);
        return templateEngine;
    }

    private ITemplateResolver fragmentTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/manage/fragment/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF8");
        templateResolver.setCheckExistence(true);
        templateResolver.setCacheable(false);
        return templateResolver;
    }

}

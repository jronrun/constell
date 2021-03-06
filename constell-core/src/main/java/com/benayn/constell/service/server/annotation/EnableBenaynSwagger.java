package com.benayn.constell.service.server.annotation;

import com.benayn.constell.service.server.annotation.condition.ConditionalOnNotProductEnvironment;
import com.benayn.constell.service.server.config.SwaggerConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableSwagger2
@ConditionalOnNotProductEnvironment
@Import(SwaggerConfiguration.class)
public @interface EnableBenaynSwagger {

}

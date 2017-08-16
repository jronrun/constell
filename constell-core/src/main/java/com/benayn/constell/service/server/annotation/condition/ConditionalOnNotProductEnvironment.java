package com.benayn.constell.service.server.annotation.condition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Conditional;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableSwagger2
@Conditional(OnProductEnvironmentCondition.class)
public @interface ConditionalOnNotProductEnvironment {

}

package com.benayn.constell.service.server.respond;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface HtmlDefine {

    String tag();

    String id() default "";

    String name() default "";

    String type() default "";

    String title() default "";

    String clazz() default "";

    String style() default "";

    String label() default "";

    String placeholder() default "";

    String[] attributes() default {};

}

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
public @interface Listable {

    /**
     * List Title, will use {@link DefineElement#value()} if not set
     */
    String value() default "";

    /**
     * Define By Fragment, if set then above set will be ignore
     */
    String fragment() default "";
}

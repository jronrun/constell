package com.benayn.constell.service.server.menu;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MenuCapability {

    /**
     * Menu Title
     */
    String value();

    /**
     * Parent Menu
     */
    String parent() default "";

    /**
     * Menu Order
     */
    int order() default 100;

    /**
     * Is New Menu
     */
    boolean fresh() default false;

    /**
     * Menu Icon
     */
    String icon() default "";

    /**
     * Menu Group, only effective for root menu ({@link MenuCapability#parent()} is empty)
     */
    String group() default "";

    /**
     * Menu Group Order
     */
    int groupOrder() default 10;

}

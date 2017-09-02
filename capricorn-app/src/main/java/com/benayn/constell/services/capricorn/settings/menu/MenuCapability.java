package com.benayn.constell.services.capricorn.settings.menu;

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
     * @return
     */
    String value();

    /**
     * Parent Menu
     * @return
     */
    String parent() default "";

    /**
     * Menu Order
     * @return
     */
    int order() default 100;

}

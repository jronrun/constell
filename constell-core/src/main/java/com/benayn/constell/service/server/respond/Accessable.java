package com.benayn.constell.service.server.respond;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Role Code or Permission Code or @{@link org.springframework.security.access.prepost.PreAuthorize} Spring EL
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Accessable {

    /**
     * View Object Create Authority
     */
    String create();
    /**
     * View Object Retrieve Authority
     */
    String retrieve();
    /**
     * View Object Update Authority
     */
    String update();
    /**
     * View Object Delete Authority
     */
    String delete();

}

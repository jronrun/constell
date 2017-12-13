package com.benayn.constell.service.server.respond;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Actionable {

    /**
     * Define edit action, default behave if true
     */
    boolean edit() default true;

    /**
     * Default edit behave if empty and {@link Actionable#edit()} is true,
     * if given an exists field name then edit behave will on the given field name column
     */
    String editField() default "";

    /**
     * Define delete action, default behave if true
     */
    boolean delete() default true;

    /**
     * Unique field value in the list
     */
    String uniqueField() default "id";

    /**
     * Define Touch Relations
     */
    DefineTouch[] relations() default {};

    /**
     * Define Action By Fragment,
     * if set then above set {@link Actionable#edit()} and {@link Actionable#delete()} will be ignore
     */
    String fragment() default "";

    /**
     * Define Append Action Fragment, different from {@link Actionable#fragment()} which is replace the default actions.
     */
    String appendFragment() default "";

    /**
     * Define create action, default behave if true
     */
    boolean create() default true;

    /**
     * Define Create Action By Fragment,
     * if set then above set {@link Actionable#create()} will be ignore.
     * trigger create form just add data-edit-id="0" to the element, eg: <a data-edit-id="0">test</a>
     */
    String createFragment() default "";

    /**
     * Define Page Ready Fragment, usually use to execute javascript when page list is ready,
     * triggers if page no changed. eg: add event to {@link Actionable#appendFragment()}
     */
    String readyFragment() default "";

}

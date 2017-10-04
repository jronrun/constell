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
public @interface DefineElement {

    /**
     * Element Label
     */
    String value();

    /**
     * Element Tag
     */
    TagName tag() default TagName.INPUT;

    /**
     * Element ID, auto generator if none, "el_{fieldName}"
     */
    String id() default "";

    /**
     * Element Name, auto generator if none, "{fieldName}"
     */
    String name() default "";

    /**
     * Element Input Type
     */
    InputType type() default InputType.TEXT;

    /**
     * Element Title
     */
    String title() default "";

    /**
     * Element Class
     */
    String clazz() default "";

    /**
     * Element CSS Style
     */
    String style() default "";

    /**
     * Element Placeholder, auto generator if none, same as label
     */
    String placeholder() default "";

    /**
     * Element Attributes
     */
    String[] attributes() default {};

    /**
     * Element Define By Fragment, if set then above set will be ignore
     */
    String fragment() default "";

}

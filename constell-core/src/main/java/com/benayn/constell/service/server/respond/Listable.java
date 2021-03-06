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
     * Date style, will use {@link DefineElement#dateStyle()} if not set
     */
    String dateStyle() default "yyyy-MM-dd HH:mm:ss";

    /**
     * Display as toggle on off widget, same as {@link HtmlTag#TOGGLE},
     * Will not work if {@link Actionable#editField()} define this field.
     * Relationship between toggle and {@link Actionable#editField()} is mutually exclusive
     */
    boolean toggleWidget() default false;

    /**
     * Element options, {@link Enum} class that implements {@link OptionValue}, will use {@link DefineElement#options()}  if not set
     */
    Class<? extends Enum> options() default Enum.class;

    /**
     * Define By Fragment, if set then above set will be ignore
     */
    String fragment() default "";
}

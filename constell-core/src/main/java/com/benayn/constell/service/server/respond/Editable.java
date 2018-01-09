package com.benayn.constell.service.server.respond;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Both define {@link Creatable} & {@link Updatable}
 */
@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Editable {

    /**
     * Element Label, will use {@link DefineElement#value()} if not set
     */
    String value() default "";

    /**
     * Element Tag, will use {@link DefineElement#value()} if not {@link HtmlTag#UNDEFINED}
     */
    HtmlTag tag() default HtmlTag.UNDEFINED;

    /**
     * Element ID, will use {@link DefineElement#id()} if not set
     */
    String id() default "";

    /**
     * Element Name, will use {@link DefineElement#name()}  if not set
     */
    String name() default "";

    /**
     * Element Input Type, will use {@link DefineElement#type()}  if not set
     */
    InputType type() default InputType.UNDEFINED;

    /**
     * Element Title, will use {@link DefineElement#title()}  if not set
     */
    String title() default "";

    /**
     * Element Class, will use {@link DefineElement#clazz()}  if not set
     */
    String clazz() default "";

    /**
     * Element CSS Style, will use {@link DefineElement#style()}  if not set
     */
    String style() default "";

    /**
     * Element readonly, will use {@link DefineElement#readonly()}  if not set
     */
    boolean readonly() default false;

    /**
     * Element disabled, will use {@link DefineElement#disabled()}  if not set
     */
    boolean disabled() default false;

    /**
     * Element Placeholder, will use {@link DefineElement#placeholder()}  if not set
     */
    String placeholder() default "";

    /**
     * Element Attributes, will use {@link DefineElement#attributes()}  if not set
     */
    String[] attributes() default {};

    /**
     * Element Hidden Flag,
     * will set {@link Editable#type()} to {@link InputType#HIDDEN}
     * if {@link Editable#tag()} is {@link HtmlTag#INPUT}
     * <br/>otherwise will append "display:none;" to {@link Editable#style()} if true
     */
    boolean hidden() default false;

    /**
     * Date style, will use {@link DefineElement#dateStyle()} if not set
     */
    String dateStyle() default "";

    /**
     * Element options, {@link Enum} class that implements {@link OptionValue}, will use {@link DefineElement#options()}  if not set
     */
    Class<? extends Enum> options() default Enum.class;

    /**
     * Element default option, value should be an instance of {@link Editable#options()}
     */
    String defaultOption() default "";

    /**
     * Define By Fragment, if set then above set will be ignore
     */
    String fragment() default "";

}

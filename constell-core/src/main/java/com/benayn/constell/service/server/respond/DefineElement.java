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
    HtmlTag tag() default HtmlTag.INPUT;

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
     * Element readonly
     */
    boolean readonly() default false;

    /**
     * Element disabled
     */
    boolean disabled() default false;

    /**
     * Element Attributes, eg: attributes = {"src=test", "custom=custom"})
     */
    String[] attributes() default {};

    /**
     * Element options, {@link Enum} class that implements {@link OptionValue}
     */
    Class<? extends Enum> options() default Enum.class;

    /**
     * Element default option, value should be an instance of {@link DefineElement#options()}
     */
    String defaultOption() default "";

    /**
     * Element Define By Fragment, if set then above set will be ignore
     */
    String fragment() default "";

    /**
     * Element date style
     */
    String dateStyle() default "yyyy-MM-dd HH:mm:ss";

}

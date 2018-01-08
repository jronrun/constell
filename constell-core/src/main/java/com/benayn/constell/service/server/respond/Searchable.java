package com.benayn.constell.service.server.respond;

import com.benayn.constell.service.server.repository.domain.ConditionTemplate;
import com.benayn.constell.service.util.Likes.Side;
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
public @interface Searchable {

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
     * Element Placeholder, will use {@link DefineElement#placeholder()}  if not set
     */
    String placeholder() default "";

    /**
     * Element Attributes, will use {@link DefineElement#attributes()}  if not set
     */
    String[] attributes() default {};

    /**
     * Date style, will use {@link DefineElement#dateStyle()} if not set
     */
    String dateStyle() default "";

    /**
     * Element options, {@link Enum} class that implements {@link OptionValue}, will use {@link DefineElement#options()}  if not set
     */
    Class<? extends Enum> options() default Enum.class;

    /**
     * Define By Fragment, if set then above set will be ignore
     */
    String fragment() default "";

    /**
     * Define search like behave
     */
    Side likeSide() default Side.BOTH;

    /**
     * Define search behave
     */
    ConditionTemplate condition() default ConditionTemplate.EQUAL_TO;

}

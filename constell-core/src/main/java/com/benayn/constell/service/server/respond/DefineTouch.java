package com.benayn.constell.service.server.respond;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class must has {@link Actionable} annotation to use
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DefineTouch {

    /**
     * Define touch behave name in the item list action column,
     * touch details define by {@link DefineTouch#view()}
     */
    String name() default "";

    /**
     * Default touch action behave if empty,
     * if given an exists field name then touch behave will on the given field name column,
     */
    String actionField() default "";

    /**
     * Define touch items show style,
     * item show as card if {@link DefineTouch#type()} is {@link TouchType#CARD}, recommend if item not too much.
     * item show as table if {@link DefineTouch#type()} is {@link TouchType#TABLE},
     */
    TouchType type() default TouchType.CARD;

    /**
     * Define Forward Touch View Object
     */
    Class<? extends Renderable> view();

    /**
     * Touch column title define by given fragment.
     * if need toggle all cell value just add data-touch-toggle-all="0" to the element, eg: <a data-touch-toggle-all="0">test</a>
     */
    String titleFragment() default "";

    /**
     * Touch column cell value define by given fragment.
     * toggleable just add data-touch-toggle="0" to the element
     */
    String cellFragment() default "";

}

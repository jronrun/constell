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
     * Define Forward Touch View Object
     */
    Class<? extends Renderable> view();

    /**
     * Maintain relation by {@link DefineTouch#view()} given class if true, or by annotation class if false
     */
    boolean master() default false;

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

    /**
     * Touch item fragment. show in touch relation list
     */
    String touchFragment() default "";

}

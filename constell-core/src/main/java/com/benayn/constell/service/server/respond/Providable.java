package com.benayn.constell.service.server.respond;

import com.benayn.constell.service.server.repository.domain.ConditionTemplate;
import com.benayn.constell.service.util.Likes.Side;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Will save or search by generic service,
 * even though none of {@link Editable}, {@link Creatable}, {@link Updatable}, {@link Searchable},
 * and never show in page
 */
@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Providable {

    /**
     * Define save & search date style
     */
    String dateStyle() default "";

    /**
     * Define search like behave
     */
    Side likeSide() default Side.BOTH;

    /**
     * Define search behave
     */
    ConditionTemplate condition() default ConditionTemplate.EQUAL_TO;

}

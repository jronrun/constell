package com.benayn.constell.services.capricorn.validator;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ PARAMETER, METHOD })
@Retention(RUNTIME)
@Constraint(validatedBy = { UniqueUserValidator.class })
@Documented
public @interface UniqueUser {

    String message() default "E-mail is already exists";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}

package com.benayn.constell.services.capricorn.settings.validator;

import com.benayn.constell.services.capricorn.request.RegisterRequest;
import com.benayn.constell.services.capricorn.service.UserService;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueUserValidator implements ConstraintValidator<UniqueUser, RegisterRequest> {

    @Autowired
    private UserService userService;

    @Override
    public void initialize(UniqueUser constraintAnnotation) {

    }

    @Override
    public boolean isValid(RegisterRequest value, ConstraintValidatorContext context) {
        return false;
    }
}

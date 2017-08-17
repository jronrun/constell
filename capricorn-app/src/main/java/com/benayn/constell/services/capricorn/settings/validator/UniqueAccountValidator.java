package com.benayn.constell.services.capricorn.settings.validator;

import com.benayn.constell.services.capricorn.request.RegisterRequest;
import com.benayn.constell.services.capricorn.service.AccountService;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueAccountValidator implements ConstraintValidator<UniqueAccount, RegisterRequest> {

    @Autowired
    private AccountService userService;

    @Override
    public void initialize(UniqueAccount constraintAnnotation) {

    }

    @Override
    public boolean isValid(RegisterRequest value, ConstraintValidatorContext context) {
        return false;
    }
}

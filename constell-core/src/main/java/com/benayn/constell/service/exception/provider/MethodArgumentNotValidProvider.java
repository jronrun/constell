package com.benayn.constell.service.exception.provider;

import com.benayn.constell.service.exception.MessageProvider;
import com.google.common.base.Joiner;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class MethodArgumentNotValidProvider implements MessageProvider<Map<String, String>> {

    @Override
    public Map<String, String> getMessages(Throwable throwable) {
        BindingResult bindingResult = ((MethodArgumentNotValidException) throwable).getBindingResult();
        return bindingResult.getFieldErrors().stream()
            .collect(Collectors.toMap(FieldError::getField, this::resolveErrorMessage));

    }

    @Override
    public String getMessage(Throwable throwable) {
        return Joiner.on(";").join(getMessages(throwable).values());
    }

    private String resolveErrorMessage(FieldError fieldError) {
        return fieldError.getField() + " " + fieldError.getDefaultMessage();
    }
}

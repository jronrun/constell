package com.benayn.constell.service.exception.provider;

import static com.google.common.collect.Maps.newHashMap;

import com.benayn.constell.service.exception.MessageProvider;
import com.google.common.base.Joiner;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

public class ConstraintViolationProvider implements MessageProvider<Map<String, String>> {

    @Override
    public Map<String, String> getMessages(Throwable throwable) {
        Set<ConstraintViolation<?>> violations = violations(throwable);
        Map<String, String> messages = newHashMap();
        for (ConstraintViolation<?> violation : violations) {
            messages.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return messages;
    }

    @Override
    public String getMessage(Throwable throwable) {
        return Joiner.on(";").join(violations(throwable).stream()
            .map(ConstraintViolation::getMessage).collect(Collectors.toList()));
    }

    private Set<ConstraintViolation<?>> violations(Throwable throwable) {
        return ((ConstraintViolationException) throwable).getConstraintViolations();
    }

}

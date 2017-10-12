package com.benayn.constell.service.exception;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.benayn.constell.service.exception.provider.ConstraintViolationProvider;
import com.benayn.constell.service.exception.provider.MethodArgumentNotValidProvider;
import com.benayn.constell.service.server.respond.Message;
import com.benayn.constell.service.server.respond.Responds;
import com.google.common.base.CharMatcher;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.support.RequestContextUtils;

@Slf4j
public abstract class BaseExceptionHandler {

    /**
     *
     */
    @Getter
    private ExceptionAttributes exceptionAttributes;
    private MessageSource messageSource;

    @ExceptionHandler({ ServiceException.class, BusinessException.class })
    public ResponseEntity<Message> handleServiceException(ServiceException ex, HttpServletRequest request) {
        Message responseBody = exceptionAttributes.getExceptionAttributes(
            ex, request, HttpStatus.CONFLICT, null);

        String message = responseBody.getMessage();
        if (!isNullOrEmpty(message) && message.startsWith("{") && message.endsWith("}")) {
            String code = CharMatcher.inRange('{', '}').trimFrom(message);
            Locale locale = RequestContextUtils.getLocale(request);

            message = messageSource.getMessage(code, ex.getArgs(), locale);
        }

        responseBody.setMessage(message);
        log.info("- {}", responseBody);

        return Responds.failure(message);
    }

    /**
     * Handles all {@link MethodArgumentNotValidException}
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ResponseEntity<Message> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, HttpServletRequest request) {
        return doHandle(ex, request, BAD_REQUEST, new MethodArgumentNotValidProvider());
    }

    /**
     * Handles all {@link AccessDeniedException}
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler({ AccessDeniedException.class })
    public ResponseEntity<Message> handleAccessDeniedException(
        AccessDeniedException ex, HttpServletRequest request) {
        return doHandle(ex, request, FORBIDDEN, null);
    }

    /**
     * Handles all {@link ConstraintViolationException}
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Message> handleConstraintViolation(
        ConstraintViolationException ex, HttpServletRequest request) {
        return doHandle(ex, request, BAD_REQUEST, new ConstraintViolationProvider());
    }

    /**
     * Handles all Exceptions not addressed by more specific <code>@ExceptionHandler</code> methods. Creates a response
     * with the Exception Attributes in the response body as JSON and a HTTP status code of 500, internal server error.
     *
     * @param exception An Exception instance.
     * @param request The HttpServletRequest in which the Exception was raised.
     * @return A ResponseEntity containing the Exception Attributes in the body and a HTTP status code 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Message> handleException(Exception exception, HttpServletRequest request) {

        log.error("> handleException");
        log.error("- Exception: ", exception);

        Message responseBody = exceptionAttributes.getExceptionAttributes(
            exception, request, INTERNAL_SERVER_ERROR);

        log.error("< handleException");
        return Responds.of(responseBody, null);
    }

    /**
     *
     */
    @SuppressWarnings("WeakerAccess")
    protected <T> ResponseEntity<Message> doHandle(Exception exception,
        HttpServletRequest request, HttpStatus httpStatus, MessageProvider<T> messageProvider) {
        Message responseBody = exceptionAttributes.getExceptionAttributes(
            exception, request, httpStatus, messageProvider);

        log.warn("- {}", responseBody);

        return Responds.of(responseBody, null);
    }

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    {
        exceptionAttributes = new DefaultExceptionAttributes();
    }

}

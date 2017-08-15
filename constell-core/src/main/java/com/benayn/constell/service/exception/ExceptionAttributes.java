package com.benayn.constell.service.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The ExceptionAttributes interface defines the behavioral contract to be
 * implemented by concrete ExceptionAttributes classes.
 *
 * Provides attributes which describe Exceptions and the context in which they
 * occurred.
 *
 * @see DefaultExceptionAttributes
 */
public interface ExceptionAttributes {

    /**
     * Returns a {@link ApiError} of exception attributes. The entity may be used to
     * display an error page or serialized into a {@link ResponseBody}.
     *
     * @param exception The Exception reported.
     * @param httpRequest The HttpServletRequest in which the Exception occurred.
     * @param httpStatus The HttpStatus value that will be used in the {@link HttpServletResponse}.
     * @return An entity of exception attributes.
     */
    ApiError getExceptionAttributes(Exception exception,
        HttpServletRequest httpRequest, HttpStatus httpStatus);

    /**
     * Returns a {@link ApiError} of exception attributes. The entity may be used to
     * display an error page or serialized into a {@link ResponseBody}.
     *
     * @param exception The Exception reported.
     * @param httpRequest The HttpServletRequest in which the Exception occurred.
     * @param httpStatus The HttpStatus value that will be used in the {@link HttpServletResponse}.
     * @param messagesProvider The messages provider {@Link Function}
     * @return An entity of exception attributes.
     */
    <T> ApiError getExceptionAttributes(Exception exception,
        HttpServletRequest httpRequest, HttpStatus httpStatus, MessageProvider<T> messagesProvider);

}

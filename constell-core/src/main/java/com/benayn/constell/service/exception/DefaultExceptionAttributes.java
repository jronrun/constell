package com.benayn.constell.service.exception;

import static com.google.common.collect.Lists.newArrayList;

import com.benayn.constell.service.server.respond.Message;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;

/**
 * The default implementation of {@link ExceptionAttributes}. This
 * implementation seeks to be similar to the {@link org.springframework.boot.autoconfigure.web.servlet.error.DefaultErrorAttributes}
 * class, but differs in the source of the attribute data. The
 * DefaultErrorAttributes class requires the exception to be thrown from the
 * Controller so that it may gather attribute values from
 * {@link RequestAttributes}. This class uses the {@link Exception},
 * {@link HttpServletRequest}, and {@link HttpStatus} values.
 *
 * Provides a {@link Message} of the following attributes when they are available:
 * <ul>
 * <li>timestamp - The time that the exception attributes were processed
 * <li>code - The HTTP status code in the response
 * <li>reasonPhrase - The HTTP status reason text
 * <li>exception - The class name of the Exception
 * <li>message - The Exception message
 * <li>path - The HTTP request servlet path when the exception was thrown
 * </ul>
 *
 * @see ExceptionAttributes
 *
 */
public class DefaultExceptionAttributes implements ExceptionAttributes {

    @Override
    public Message getExceptionAttributes(Exception exception,
        HttpServletRequest httpRequest, HttpStatus httpStatus) {
        return getExceptionAttributes(exception, httpRequest, httpStatus, null);
    }

    @Override
    public <T> Message getExceptionAttributes(Exception exception,
        HttpServletRequest httpRequest, HttpStatus httpStatus, MessageProvider<T> messagesProvider) {

        Message apiError = new Message();

        apiError.setTimestamp(new Date());
        addHttpStatus(apiError, httpStatus);
        addExceptionDetail(apiError, exception, messagesProvider);
        addPath(apiError, httpRequest);

        return apiError;
    }

    /**
     * Adds the status and error attribute values from the {@link HttpStatus}
     * value.
     * @param apiError The Entity of exception attributes.
     * @param httpStatus The HttpStatus enum value.
     */
    private void addHttpStatus(Message apiError, HttpStatus httpStatus) {
        apiError.setCode(httpStatus.value());
        apiError.setReasonPhrase(httpStatus.getReasonPhrase());
    }

    /**
     * Adds the exception and message attribute values from the
     * {@link Exception}.
     * @param apiError The Entity of exception attributes.
     * @param exception The Exception object.
     */
    private <T> void addExceptionDetail(Message apiError,
        Exception exception, MessageProvider<T> messagesProvider) {
        apiError.setException(exception.getClass().getName());

        if (null != messagesProvider) {
            apiError.setMessage(messagesProvider.getMessage(exception));
            apiError.setMessages(messagesProvider.getMessages(exception));
        } else {
            apiError.setMessage(exception.getMessage());
            apiError.setMessages(newArrayList());
        }
    }

    /**
     * Adds the path attribute value from the {@link HttpServletRequest}.
     * @param apiError The Entity of exception attributes.
     * @param httpRequest The HttpServletRequest object.
     */
    private void addPath(Message apiError, HttpServletRequest httpRequest) {
        apiError.setPath(httpRequest.getServletPath());
    }

}

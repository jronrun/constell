package com.benayn.constell.service.server.respond;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Message {

    /**
     * {@link HttpStatus} value
     */
    private int code;

    /**
     * Message Result
     */
    private Object result;

    /**
     * message
     */
    private String message;
    private Date timestamp;

    /**
     * path attribute value from the {@link HttpServletRequest} if throw an exception
     */
    private String path;

    /**
     * error attribute values from the {@link HttpStatus} if throw an exception
     */
    private String reasonPhrase;
    /**
     * exception attribute values from the {@link Exception} if throw an exception
     */
    private String exception;
    /**
     * messages attribute values from the {@link Exception} if throw an exception
     */
    private Object messages;

    public static Message of(HttpStatus status, Object result, String message) {
        return of(status.value(), result, message, new Date(), null, status.getReasonPhrase(), null, null);
    }

}

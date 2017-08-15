package com.benayn.constell.service.exception;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ApiError {

    /**
     * path attribute value from the {@link HttpServletRequest}..
     */
    private String path;
    /**
     * status attribute values from the {@link HttpStatus}
     */
    private int status;
    /**
     * error attribute values from the {@link HttpStatus}
     */
    private String error;
    /**
     * exception attribute values from the {@link Exception}.
     */
    private String exception;
    /**
     * message attribute values from the {@link Exception}.
     */
    private String message;
    /**
     * messages attribute values from the {@link Exception}.
     */
    private Object messages;
    private Date timestamp;

}

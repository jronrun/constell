package com.benayn.constell.service.exception;

import lombok.Getter;

public class ServiceException extends Exception {

    public static final int DEFAULT_CODE = 0;
    private static final long serialVersionUID = -3653068626422310886L;

    @Getter
    private Object[] args = null;
    @Getter
    private int code;

    public ServiceException(final String message) {
        this(DEFAULT_CODE, message);
    }

    public ServiceException(final String message, Object[] messageArgs) {
        this(0, message, messageArgs);
    }

    public ServiceException(final int code, final String message) {
        super(message);
    }

    public ServiceException(final int code, final String message, Object[] messageArgs) {
        super(message);
        this.code = code;
        this.args = messageArgs;
    }

    public ServiceException(final Throwable cause) {
        super(cause);
    }

    public ServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

}

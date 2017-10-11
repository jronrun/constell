package com.benayn.constell.service.exception;

import lombok.Getter;

public class ServiceException extends Exception {

    private static final long serialVersionUID = -3653068626422310886L;

    @Getter
    private Object[] args = null;

    public ServiceException(final String message) {
        super(message);
    }

    public ServiceException(final String message, Object[] messageArgs) {
        super(message);
        this.args = messageArgs;
    }

    public ServiceException(final Throwable cause) {
        super(cause);
    }

    public ServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

}

package com.benayn.constell.service.exception;

public class BusinessException extends ServiceException {

    private static final long serialVersionUID = 6208730315040081319L;

    public BusinessException(final String message) {
        super(message);
    }

    public BusinessException(final String message, Object[] messageArgs) {
        super(message, messageArgs);
    }

    public BusinessException(final Throwable cause) {
        super(cause);
    }

    public BusinessException(final String message, final Throwable cause) {
        super(message, cause);
    }

}

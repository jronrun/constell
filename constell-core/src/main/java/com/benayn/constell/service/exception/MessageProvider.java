package com.benayn.constell.service.exception;

public interface MessageProvider<T> {

    /**
     * Returns messages with given throwable
     * @param throwable
     * @return
     */
    T getMessages(Throwable throwable);

    /**
     * Returns message with given throwable
     * @param throwable
     * @return
     */
    String getMessage(Throwable throwable);
}

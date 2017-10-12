package com.benayn.constell.service.server.respond;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

public class Responds {

    public static <T> ResponseEntity<Message> success(@Nullable T body) {
        return of(HttpStatus.OK, body);
    }

    public static ResponseEntity<Message> failure(String message) {
        return failure(HttpStatus.CONFLICT, message);
    }

    public static ResponseEntity<Message> failure(HttpStatus status, String message) {
        return of(status, null, message);
    }

    public static <T> ResponseEntity<Message> of(HttpStatus status, @Nullable T body) {
        return of(Message.of(status, body, null), null);
    }

    public static <T> ResponseEntity<Message> of(HttpStatus status, @Nullable T body, String message) {
        return of(Message.of(status, body, message), null);
    }

    public static ResponseEntity<Message> of(Message message, @Nullable MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(message, headers, HttpStatus.valueOf(message.getCode()));
    }

}

package com.benayn.constell.service.server.respond;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

public class Responds {

    public static <T> ResponseEntity<Message> success(@Nullable T body) {
        return of(body, HttpStatus.OK);
    }

    public static <T> ResponseEntity<Message> failure(@Nullable T body) {
        return of(body, HttpStatus.CONFLICT);
    }

    public static ResponseEntity<Message> of(HttpStatus status) {
        return new ResponseEntity<>(status);
    }

    public static <T> ResponseEntity<Message> of(@Nullable T body, HttpStatus status) {
        return new ResponseEntity<>(Message.of(body), status);
    }

    public static ResponseEntity<Message> of(MultiValueMap<String, String> headers, HttpStatus status) {
        return new ResponseEntity<>(headers, status);
    }

    public static <T> ResponseEntity<Message> of(@Nullable T body,
        @Nullable MultiValueMap<String, String> headers, HttpStatus status) {
        return new ResponseEntity<>(Message.of(body), headers, status);
    }

}

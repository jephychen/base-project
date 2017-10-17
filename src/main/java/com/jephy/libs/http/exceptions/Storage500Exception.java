package com.jephy.libs.http.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class Storage500Exception extends RuntimeException {

    public Storage500Exception(String message) {
        super(message);
    }

    public Storage500Exception(String message, Throwable cause) {
        super(message, cause);
    }
}

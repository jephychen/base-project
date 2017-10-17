package com.jephy.libs.http.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class StorageFileNotFound500Exception extends Storage500Exception {

    public StorageFileNotFound500Exception(String message) {
        super(message);
    }

    public StorageFileNotFound500Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
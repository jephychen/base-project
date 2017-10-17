package com.jephy.libs.http.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by chenshijue on 2017/9/22.
 */

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ServiceUnavailable503Exception extends RuntimeException {

    public ServiceUnavailable503Exception(String message){
        super(message);
    }

}

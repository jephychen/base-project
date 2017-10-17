package com.jephy.libs.http.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by chenshijue on 2017/9/22.
 */

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class Unauthorized401Exception extends RuntimeException {

    public Unauthorized401Exception(String message){
        super(message);
    }

}

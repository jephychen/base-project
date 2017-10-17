package com.jephy.libs.httpexceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by chenshijue on 2017/9/22.
 */

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerError500Exception extends RuntimeException {

    public InternalServerError500Exception(String message){
        super(message);
    }

}

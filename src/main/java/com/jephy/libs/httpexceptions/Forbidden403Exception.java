package com.jephy.libs.httpexceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by chenshijue on 2017/9/22.
 */

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class Forbidden403Exception extends RuntimeException {

    public Forbidden403Exception(String message){
        super(message);
    }

}

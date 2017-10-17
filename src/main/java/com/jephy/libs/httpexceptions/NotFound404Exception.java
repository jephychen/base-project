package com.jephy.libs.httpexceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by chenshijue on 2017/9/22.
 */

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFound404Exception extends RuntimeException {

    public NotFound404Exception(String message){
        super(message);
    }

}

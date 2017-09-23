package com.jephy.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by chenshijue on 2017/9/22.
 */

@RestController
public class DemoController {

    @RequestMapping("/test")
    public String demo(String code, HttpServletRequest request, HttpServletResponse response){
        response.setStatus(500);
        return request.getMethod() + code;
    }

}

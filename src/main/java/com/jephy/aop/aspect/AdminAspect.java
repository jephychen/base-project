package com.jephy.aop.aspect;

import com.jephy.utils.httpexceptions.Forbidden403Exception;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by chenshijue on 2017/9/25.
 */

@Aspect
public class AdminAspect {

    @Pointcut("@annotation(com.jephy.aop.annotation.AuthAdmin)")
    public void pointcut(){}

    @Autowired
    private HttpServletRequest request;

    @Before("pointcut()")
    public void checkAuth() {
        throw new Forbidden403Exception(request.getMethod());
    }

}

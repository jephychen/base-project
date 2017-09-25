package com.jephy.aop.aspect;

import com.jephy.utils.httpexceptions.Forbidden403Exception;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by chenshijue on 2017/9/25.
 */

@Aspect
public class AdminAspect {

    @Pointcut("@annotation(com.jephy.aop.annotation.AuthAdmin)")
    public void pointcut(){}

    @Around("pointcut()")
    public Object checkAuth(ProceedingJoinPoint jp) {
        throw new Forbidden403Exception("not auth");
    }
}

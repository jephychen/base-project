package com.jephy.aop.aspect;

import com.jephy.libs.JwtHelper;
import com.jephy.libs.http.CookieHelper;
import com.jephy.models.User;
import com.jephy.utils.httpexceptions.BadRequest400Exception;
import com.jephy.utils.httpexceptions.Forbidden403Exception;
import com.mongodb.BasicDBObject;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by chenshijue on 2017/9/25.
 */

@Aspect
public class AuthAspect {

    @Pointcut("@annotation(com.jephy.aop.annotation.AuthAdmin)")
    public void pointcutAdmin(){}

    @Pointcut("@annotation(com.jephy.aop.annotation.AuthCommon)")
    public void pointcutCommon(){}

    @Autowired
    private HttpServletRequest request;

    @Before("pointcutAdmin()")
    public void checkAuthAdmin() {
        doCheckRole(User.ROLE_ADMIN);
    }

    @Before("pointcutCommon()")
    public void checkAuthCommon() {
        doCheckRole(User.ROLE_COMMON);
    }

    protected void doCheckRole(String role){
        Cookie cookie = CookieHelper.getCookie(request, "jwt");
        if (cookie == null)
            throw new BadRequest400Exception("can't get jwt");

        BasicDBObject payload = null;
        try {
            JwtHelper.getPayload(cookie.getValue());
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadRequest400Exception("can't parse jwt");
        }
        if (payload == null) throw new BadRequest400Exception("can't parse jwt");

        if (!payload.get("role").equals(role)) throw new Forbidden403Exception("user not authorized");
    }

}

package com.jephy.aop.aspect;

import com.jephy.api.JwtHandler;
import com.jephy.libs.Const;
import com.jephy.libs.JwtHelper;
import com.jephy.libs.http.CookieHelper;
import com.jephy.models.User;
import com.jephy.utils.httpexceptions.BadRequest400Exception;
import com.jephy.utils.httpexceptions.Forbidden403Exception;
import com.mongodb.BasicDBObject;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by chenshijue on 2017/9/25.
 */

@Component
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
        Cookie cookie = CookieHelper.getCookie(request, Const.JWT_COOKIE_NAME);
        if (cookie == null)
            throw new BadRequest400Exception("please login");

        BasicDBObject payload = null;
        StringBuffer jwtReason = new StringBuffer();
        try {
            payload = JwtHelper.getPayload(cookie.getValue(), jwtReason);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadRequest400Exception(jwtReason.toString());
        }
        if (payload == null) throw new BadRequest400Exception(jwtReason.toString());

        //如果为管理员则直接返回
        if (payload.get("role").equals("admin")) return;

        if (!payload.get("role").equals(role)) throw new Forbidden403Exception("user not authorized");
    }

    @Autowired
    private HttpServletResponse response;

    @Value("${web.session.expire.time}")
    private int sessionExpireMinute;

    @Autowired
    private JwtHandler jwtHandler;

    @AfterReturning("pointcutAdmin()")
    public void resetCookieAdmin(){
        resetCookie();
    }

    @AfterReturning("pointcutCommon()")
    public void resetCookieCommon(){
        resetCookie();
    }

    protected void resetCookie(){
        Cookie cookie = CookieHelper.getCookie(request, Const.JWT_COOKIE_NAME);
        if (cookie == null)  return;

        BasicDBObject payload = null;
        try {
            payload = JwtHelper.getPayload(cookie.getValue(), null);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (payload == null) return;

        String jwt = null;
        try {
            jwt = JwtHelper.genJwt(payload.toMap(), sessionExpireMinute * 60);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        if (jwt == null) return;

        jwtHandler.handleJwt(jwt, response);
    }

}

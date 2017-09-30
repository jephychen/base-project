package com.jephy.services.jwt;

import com.jephy.api.JwtHandler;
import com.jephy.libs.Const;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by chenshijue on 2017/9/30.
 */

@Service
public class CookieJwtHandler implements JwtHandler {

    @Override
    public void handleJwt(String jwt, HttpServletResponse response) {
        Cookie jwtCookie = new Cookie(Const.JWT_COOKIE_NAME, jwt);
        jwtCookie.setMaxAge(Const.MAX_SESSION_EXPIRE);
        response.addCookie(jwtCookie);
    }

}

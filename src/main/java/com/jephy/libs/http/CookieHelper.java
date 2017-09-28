package com.jephy.libs.http;

import com.jephy.libs.Const;
import com.jephy.libs.JwtHelper;
import com.mongodb.BasicDBObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by chenshijue on 2017/9/27.
 */
public class CookieHelper {

    public static Cookie getCookie(HttpServletRequest request, String cookieName){
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie cookie : cookies){
            if (cookie.getName().equals(cookieName))
                return cookie;
        }
        return null;
    }

    public static BasicDBObject getJwtPayload(HttpServletRequest request){
        Cookie jwtCookie = getCookie(request, Const.JWT_COOKIE_NAME);
        if (jwtCookie == null) return null;

        BasicDBObject payload = null;
        try {
            payload = JwtHelper.getPayload(jwtCookie.getValue(), null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return payload;
    }

}

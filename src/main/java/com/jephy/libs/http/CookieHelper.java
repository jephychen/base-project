package com.jephy.libs.http;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

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

}

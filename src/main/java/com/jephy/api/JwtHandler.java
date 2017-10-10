package com.jephy.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by chenshijue on 2017/9/30.
 */
public interface JwtHandler {

    void handleJwt(String jwt, HttpServletResponse response);

    void removeJwt(HttpServletRequest request, HttpServletResponse response);

}

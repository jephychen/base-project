package com.jephy.controllers;

import com.jephy.libs.Const;
import com.jephy.libs.JwtHelper;
import com.jephy.libs.http.CookieHelper;
import com.jephy.libs.json.JsonHelper;
import com.jephy.models.User;
import com.jephy.services.UserService;
import com.jephy.utils.httpexceptions.BadRequest400Exception;
import com.jephy.utils.httpexceptions.InternalServerError500Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenshijue on 2017/9/27.
 */

@RestController
@RequestMapping("/session")
public class SessionController {

    @Value("${web.session.expire.time}")
    private int sessionExpireMinute;

    public static final int MAX_SESSION_EXPIRE = 60 * 60 * 24 * 30;

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST)
    public void login(@RequestBody String userInfo, HttpServletResponse response){
        if (userInfo == null) throw new BadRequest400Exception("not user info found");

        User user = null;
        try {
            user = (User) JsonHelper.parse(User.class, userInfo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequest400Exception("userInfo invalid");
        }

        if (user.getEmail() == null && user.getPhone() == null){
            throw new BadRequest400Exception("email or phone must be provided");
        }

        //检查用户合法性
        User existUser = userService.checkLogin(user);

        Map<String, String> payload = new HashMap<>();
        payload.put("id", existUser.getId());
        payload.put("role", existUser.getRole());
        String jwt = null;
        try {
            jwt = JwtHelper.genJwt(payload, sessionExpireMinute * 60);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new InternalServerError500Exception("login error");
        }

        Cookie jwtCookie = new Cookie("jwt", jwt);
        jwtCookie.setMaxAge(MAX_SESSION_EXPIRE);
        response.addCookie(jwtCookie);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie jwtCookie = CookieHelper.getCookie(request, Const.JWT_COOKIE_NAME);

        if (jwtCookie == null)  return;
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);
    }

}

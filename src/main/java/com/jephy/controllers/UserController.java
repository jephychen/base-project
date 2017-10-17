package com.jephy.controllers;

import com.jephy.aop.annotation.AuthAdmin;
import com.jephy.aop.annotation.AuthCommon;
import com.jephy.libs.http.CookieHelper;
import com.jephy.libs.json.JsonHelper;
import com.jephy.models.User;
import com.jephy.services.UserService;
import com.jephy.libs.httpexceptions.BadRequest400Exception;
import com.jephy.libs.httpexceptions.Forbidden403Exception;
import com.mongodb.BasicDBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by chenshijue on 2017/9/22.
 */

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @AuthCommon
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable String id){
        return userService.findById(id);
    }

    @AuthAdmin
    @RequestMapping(method = RequestMethod.GET)
    public Page<User> getUsers(
            @PageableDefault(value = 10, sort = {"created"}, direction = Sort.Direction.DESC) Pageable pageable,
            String gender){
        if (gender != null) {
            return userService.findByGender(gender, pageable);
        }
        return userService.findAll(pageable);
    }

    @RequestMapping(method = RequestMethod.POST)
    public User addUser(@RequestBody String userJson){
        //校验json
        User user = null;
        try {
            user = (User) JsonHelper.parse(User.class, userJson);
        } catch (Exception e) {
            throw new BadRequest400Exception("userJson invalid");
        }

        return userService.addUser(user);
    }

    @AuthCommon
    @RequestMapping(method = RequestMethod.PUT)
    public User updateUser(@RequestBody String userJson, HttpServletRequest request){
        //校验json
        User user = null;
        try {
            user = (User) JsonHelper.parse(User.class, userJson);
        } catch (Exception e) {
            throw new BadRequest400Exception("userJson invalid");
        }

        //如果普通用户不是修改的自己则无权限
        BasicDBObject jwtPayload = CookieHelper.getJwtPayload(request);
        if (!jwtPayload.get("role").equals("admin") && !user.getId().equals(jwtPayload.get("id")))
            throw new Forbidden403Exception("user not authorized");

        return userService.updateUser(user);
    }

    @AuthAdmin
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable String id){
        userService.deleteUser(id);
    }

}

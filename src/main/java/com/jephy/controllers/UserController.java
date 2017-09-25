package com.jephy.controllers;

import com.jephy.aop.annotation.AuthAdmin;
import com.jephy.libs.json.JsonHelper;
import com.jephy.models.User;
import com.jephy.services.UserService;
import com.jephy.utils.httpexceptions.BadRequest400Exception;
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

    @AuthAdmin
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable String id, HttpServletRequest request){
        String jwt = request.getHeader("jwt");
        return userService.findById(id);
    }

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

    @RequestMapping(method = RequestMethod.PUT)
    public User updateUser(@RequestBody String userJson){
        //校验json
        User user = null;
        try {
            user = (User) JsonHelper.parse(User.class, userJson);
        } catch (Exception e) {
            throw new BadRequest400Exception("userJson invalid");
        }

        return userService.updateUser(user);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable String id){
        userService.deleteUser(id);
    }

}

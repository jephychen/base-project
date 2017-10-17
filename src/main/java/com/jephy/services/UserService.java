package com.jephy.services;

import com.jephy.libs.Const;
import com.jephy.libs.EncryptHelper;
import com.jephy.libs.ObjectHelper;
import com.jephy.models.User;
import com.jephy.repositories.UserRepository;
import com.jephy.libs.httpexceptions.BadRequest400Exception;
import com.jephy.libs.httpexceptions.Forbidden403Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * Created by chenshijue on 2017/9/22.
 */

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public User findById(String id){
        return userRepository.findById(id);
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User findByPhone(String phone){
        return userRepository.findByPhone(phone);
    }

    public Page<User> findByGender(String gender, Pageable pageable) {
        return userRepository.findByGender(gender, pageable);
    }

    public Page<User> findAll(Pageable pageable){
        return userRepository.findAll(pageable);
    }

    public User addUser(User user){
        if (user.getEmail() == null && user.getPhone() == null)
            throw new BadRequest400Exception("one of phone and email must be provided");
        if (user.getPassword() == null)
            throw new BadRequest400Exception("password should be provided");

        //检查phone和email是否重复
        User existedUser = null;
        if (user.getEmail() != null) existedUser = userRepository.findByEmail(user.getEmail());
        if (user.getPhone() != null && existedUser == null)
            existedUser = userRepository.findByPhone(user.getPhone());
        if (existedUser != null)
            throw new Forbidden403Exception("Phone or email exists");

        //加密密码
        user.setPassword(EncryptHelper.encrypt(user.getPassword(), Const.PASSWORD_KEY));

        user.setRole(User.ROLE_COMMON);
        user.setCreated(System.currentTimeMillis());
        return userRepository.save(user);
    }

    public User updateUser(User user){
        if (user.getId() == null)
            throw new BadRequest400Exception("user id must be provided");

        //检查email或者phone是否重复
        Query query = new Query();
        query.addCriteria(new Criteria("id").ne(user.getId()));
        if (user.getEmail() != null){
            query.addCriteria(new Criteria("email").is(user.getEmail()));
            long count = mongoTemplate.count(query, User.class);
            if (count > 0)
                throw new Forbidden403Exception("email exists");
        }
        if (user.getPhone() != null){
            query.addCriteria(new Criteria("phone").is(user.getPhone()));
            long count = mongoTemplate.count(query, User.class);
            if (count > 0)
                throw new Forbidden403Exception("phone exists");
        }

        User dbUser = userRepository.findById(user.getId());

        try {
            ObjectHelper.updateObj(dbUser, user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //加密密码
        if (user.getPassword() != null)
            dbUser.setPassword(EncryptHelper.encrypt(user.getPassword(), Const.PASSWORD_KEY));

        dbUser.setModified(System.currentTimeMillis());
        return userRepository.save(dbUser);
    }

    public void deleteUser(String id){
        userRepository.delete(id);
    }

    public User checkLogin(User user){
        User existUser = null;
        if (user.getEmail() != null)
            existUser = userRepository.findByEmail(user.getEmail());
        else if (user.getPhone() != null)
            existUser = userRepository.findByPhone(user.getPhone());

        //如果找不到用户
        if (existUser == null) throw new Forbidden403Exception("no user found");

        //加密后匹配
        String encryPwd = EncryptHelper.encrypt(user.getPassword(), Const.PASSWORD_KEY);
        if (!existUser.getPassword().equals(encryPwd))
            throw new Forbidden403Exception("password error");

        return existUser;
    }

}

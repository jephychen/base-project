package com.jephy.services;

import com.jephy.libs.ObjectHelper;
import com.jephy.libs.json.JsonHelper;
import com.jephy.models.User;
import com.jephy.repositories.UserRepository;
import com.jephy.utils.httpexceptions.BadRequest400Exception;
import com.jephy.utils.httpexceptions.Forbidden403Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User addUser(User user){
        if (user.getEmail() == null && user.getPhone() == null)
            throw new BadRequest400Exception("one of phone and email must be provided");

        //检查phone和email是否重复
        User existedUser = user.getEmail() == null ? userRepository.findByPhone(user.getPhone())
                : userRepository.findByEmail(user.getEmail());
        if (existedUser != null)
            throw new Forbidden403Exception("Phone or email exists");

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
            ObjectHelper.fillNewObj(dbUser, user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userRepository.save(dbUser);
    }

    public void deleteUser(String id){
        userRepository.delete(id);
    }

}

package com.jephy.repositories;

import com.jephy.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by chenshijue on 2017/9/22.
 */
public interface UserRepository extends MongoRepository<User, String> {

    public User findById(String id);

    public User findByPhone(String phone);

    public User findByEmail(String email);

    public Page<User> findByGender(String gender, Pageable pageable);

}

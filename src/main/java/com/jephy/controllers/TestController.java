package com.jephy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by chenshijue on 2017/10/17.
 */

@RestController
public class TestController {

    @Autowired
    private RedisTemplate redisTemplate;
    
    @Resource(name = "redisTemplate")
    private SetOperations<String, String> setOperations;

    @Autowired
    private JedisPool jedisPool;

    @RequestMapping(value = "/test", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public Set<String> test(){
        return setOperations.members("spring");
    }

    @RequestMapping(value = "/test3", method = RequestMethod.GET)
    public Object test3(){
        Jedis jedis = jedisPool.getResource();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Jedis j = jedisPool.getResource();
                sleep(1);
                j.set("jedispool", "niub");
            }
        }).start();

        jedis.watch("jedispool");
        Transaction tx = jedis.multi();
        tx.set("jedispool", "from test3");
        sleep(2);
        tx.set("jedispool", "from test3 again");
        //tx.incr("jedispool");
        return tx.exec();
    }

    private void sleep(int seconds){
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/test1",method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public Object test1(){
        redisTemplate.opsForValue().set("spring", "ccc");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                redisTemplate.opsForValue().set("spring", "zzz");
            }
        }).start();
        return redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch("spring");
                operations.multi();
                operations.opsForValue().set("spring", "aaa");
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                operations.opsForValue().set("spring", "bbb");
                return operations.exec();
            }
        });

        //return (String) redisTemplate.opsForValue().get("spring");
    }
}

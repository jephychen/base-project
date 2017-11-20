package com.jephy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by chenshijue on 2017/11/19.
 */
@Configuration
public class RedisConfig {

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(){
        JedisConnectionFactory jfc = new JedisConnectionFactory();
        jfc.setHostName("127.0.0.1");
        jfc.setPort(6379);
        jfc.setDatabase(0);
        return jfc;
    }

    @Bean
    public RedisTemplate redisTemplate(JedisConnectionFactory jedisConnectionFactory){
        RedisTemplate rt = new RedisTemplate();
        rt.setConnectionFactory(jedisConnectionFactory);
        return rt;
    }

    @Bean
    public JedisPool jedisPool(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(20);
        poolConfig.setMaxIdle(10);
        JedisPool pool = new JedisPool(poolConfig, "127.0.0.1", 6379);
        return pool;
    }
}

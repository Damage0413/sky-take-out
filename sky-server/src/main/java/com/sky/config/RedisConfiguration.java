package com.sky.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("初始化redis模板对象");
        RedisTemplate redisTemplate = new RedisTemplate<>();
        // 设置redis连接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置redis key的序列化器
        // 所谓序列化器是用来将对象序列化为字符串存储在redis上，或者将redis上存储的字符串反序列化为对象的工具
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;

    }

}
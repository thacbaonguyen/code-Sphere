package com.thacbao.codeSphere.services.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value, int timeCache, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value,timeCache, timeUnit);
        log.info("set key:{}, value:{}, time:{}", key, value, timeUnit);
    }

    public <T> T get(String key){
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        try {
            @SuppressWarnings("unchecked")
            T result = (T) value;
            log.info("get key:{}", key);
            return result;
        } catch (ClassCastException e) {
            throw new IllegalStateException("Cannot cast Redis value to the requested type for key: " + key, e);
        }
    }

    public void delete(String key) {
        redisTemplate.delete(redisTemplate.keys(key + "*"));
        log.info("delete cache key:{}", key);
    }
}

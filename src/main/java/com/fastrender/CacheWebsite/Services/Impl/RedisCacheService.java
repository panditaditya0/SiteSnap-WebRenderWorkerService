package com.fastrender.CacheWebsite.Services.Impl;

import com.fastrender.CacheWebsite.Services.CacheService;
import com.fastrender.CacheWebsite.model.CacheDataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisCacheService implements CacheService {

    private RedisTemplate<String, Object> redisTemplate;

    private HashOperations<String, String, CacheDataModel> hashOperations;

    @Autowired
    public RedisCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }


    @Override
    public <T> void saveToDb(String expirationTime, String websiteDomain, String websiteUrl, T data) {
        hashOperations.put(websiteDomain, websiteUrl, (CacheDataModel) data);
        redisTemplate.expire(websiteDomain, Duration.ofMinutes(Integer.valueOf(expirationTime)));
    }

    @Override
    public <T> boolean updateFromDb(T data) {
        return false;
    }

    @Override
    public <T> boolean deleteFromDb(T data) {
        return false;
    }

    @Override
    public <T> boolean existsFromDb(T data) {
        return false;
    }

    @Override
    public <T> T loadFromDb(T data) {
        return null;
    }
}

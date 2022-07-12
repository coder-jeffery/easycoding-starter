package com.easycoding.kylin.starter.redis;


import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yingzhou.wei
 * @date 2018/5/29
 */
@Component("redisCache")
@ConditionalOnClass({RedissonClient.class})
public class RedisCache<T> implements ICache<T> {
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void putAll(String mapName, Map<String, T> map, long time, TimeUnit timeUnit) {
        RMap<Object, Object> rMap = redissonClient.getMap(mapName);
        rMap.putAll(map);
        rMap.expire(time, timeUnit);
    }

    @Override
    public void put(String mapName, String key, T value) {
        redissonClient.getMap(mapName).put(key, value);
    }

    @Override
    public T get(String mapName, String key) {
        RMap<Object, Object> map = redissonClient.getMap(mapName);
        if (map == null) return null;
        return (T) map.get(key);
    }

    @Override
    public Map<String, T> getMap(String mapName) {
        return redissonClient.getMap(mapName);
    }

    @Override
    public void put(String key, T value, long time, TimeUnit timeUnit) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        bucket.set(value, time, timeUnit);
    }

    @Override
    public void put(String key, T value) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        bucket.set(value);
    }

    @Override
    public T get(String key) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    @Override
    public boolean exists(String key) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.isExists();
    }

    @Override
    public boolean trySet(String key, T value, long time, TimeUnit timeUnit) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        boolean isSuccess = bucket.trySet(value);
        if (isSuccess) {
            bucket.expire(time, timeUnit);
        }
        return isSuccess;
    }

    @Override
    public long delete(String key) {
        RKeys keys = redissonClient.getKeys();
        return keys.delete(key);
    }

    @Override
    public long deleteByPattern(String pattern) {
        RKeys keys = redissonClient.getKeys();
        return keys.deleteByPattern(pattern);
    }

    @Override
    public long incr(String key) {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        return atomicLong.incrementAndGet();
    }

    @Override
    public long decr(String key) {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        return atomicLong.decrementAndGet();
    }

    @Override
    public long incrAndExpireAt(String key, Date expireAt) {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        atomicLong.expireAt(expireAt);
        return atomicLong.incrementAndGet();
    }

    @Override
    public long incrbyAndExpireAt(String key, long incr, Date expireAt) {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        atomicLong.expireAt(expireAt);
        return atomicLong.addAndGet(incr);
    }

    @Override
    public void expireAt(String key, long time, TimeUnit timeUnit) {
        RBucket bucket = redissonClient.getBucket(key);
        bucket.expire(time, timeUnit);
    }

    @Override
    public void expireAt(String key, Date effectEndTime) {
        RBucket bucket = redissonClient.getBucket(key);
        bucket.expireAt(effectEndTime);
    }
}

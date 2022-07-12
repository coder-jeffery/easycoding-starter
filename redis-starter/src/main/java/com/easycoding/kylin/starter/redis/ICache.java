package com.easycoding.kylin.starter.redis;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yingzhou.wei
 * @Description
 * @date 2019/4/15
 */
public interface ICache<T> {

    void putAll(String mapName, Map<String, T> map, long time, TimeUnit timeUnit);

    void put(String mapName, String key, T value);

    T get(String mapName, String key);

    Map<String,T> getMap(String mapName);

    void put(String key, T value, long time, TimeUnit timeUnit);

    void put(String key, T value);

    T get(String key);

    boolean exists(String key);

    boolean trySet(String key, T value, long time, TimeUnit timeUnit);

    long delete(String key);

    long deleteByPattern(String pattern);

    long incr(String key);

    long decr(String key);

    long incrAndExpireAt(String key, Date expireAt);

    long incrbyAndExpireAt(String key, long incr, Date expireAt);

    void expireAt(String key, long time, TimeUnit timeUnit);

    void expireAt(String key, Date effectEndTime);
}

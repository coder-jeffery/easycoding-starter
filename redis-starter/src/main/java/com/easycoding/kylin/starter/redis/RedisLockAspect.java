package com.easycoding.kylin.starter.redis;

import com.easycoding.kylin.starter.json.util.JsonUtil;
import org.apache.commons.beanutils.BeanMap;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: yu.yue
 * @Date: Created in 2018/5/24 10:19
 * @Modified By:
 * @Description:
 */
@Aspect
@Component
public class RedisLockAspect {
    @Autowired
    private RedissonClient redissonClient;

    @Around(value = "@annotation(com.easycoding.kylin.starter.redis.RedisLocked) && @annotation(locked)")
    public Object around(ProceedingJoinPoint joinPoint, RedisLocked locked) throws Throwable {
        String key = locked.key();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            key = matchKey(args[i], key, String.format("args[%d]", i));
        }
        RLock lock = redissonClient.getLock(key);
        boolean islock = false;
        try {
            islock = tryLock(locked, lock);
            if (islock) {
                return joinPoint.proceed(joinPoint.getArgs());
            }
            if (Throwable.class.isAssignableFrom(locked.returnValue().valueClass())) {
                Constructor constructor = locked.returnValue().valueClass().getConstructor(String.class);
                throw (Throwable) constructor.newInstance(locked.returnValue().value());
            }
            if (locked.returnValue().valueClass().equals(void.class)) {
                return null;
            }
            return JsonUtil.fromJson(locked.returnValue().value(), locked.returnValue().valueClass());
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (islock) {
                    lock.unlock();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean tryLock(RedisLocked locked, RLock lock) throws InterruptedException {
        boolean islock = false;
        if (locked.blockTime().value() == 0) {
            islock = lock.tryLock(0, 10, TimeUnit.SECONDS);
        } else {
            switch (locked.blockTime().timeType()) {
                case MILLISECONDS:
                    islock = lock.tryLock(locked.blockTime().value() * 2, locked.blockTime().value(), TimeUnit.MILLISECONDS);
                    break;
                case SECONDS:
                    islock = lock.tryLock(locked.blockTime().value() * 2, locked.blockTime().value(), TimeUnit.SECONDS);
                    break;
                case MINUTES:
                    islock = lock.tryLock(locked.blockTime().value() * 2, locked.blockTime().value(), TimeUnit.MINUTES);
                    break;
                case HOURS:
                    islock = lock.tryLock(locked.blockTime().value() * 2, locked.blockTime().value(), TimeUnit.HOURS);
                    break;
            }
        }
        return islock;
    }

    private String matchKey(Object obj, String value, String replaceStr) {
        String returnValue = value;
        if (value.contains(replaceStr + ".")) {
            BeanMap map = new BeanMap(obj);
            Object[] matchObj = map.keySet().stream().filter(k -> value.contains(String.format("%s.%s", replaceStr, k.toString()))).toArray();
            if (matchObj != null && matchObj.length != 0) {
                for (Object o : matchObj) {
                    returnValue = matchKey(map.get(o), returnValue, String.format("%s.%s", replaceStr, o.toString()));
                }
            }
        } else if (value.contains(replaceStr)) {
            returnValue = returnValue.replace(replaceStr, obj == null ? "" : obj.toString());
        }
        return returnValue;
    }
}

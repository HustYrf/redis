package com.learn.redis.RedisReentrantLock;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName RedisReentrantLock
 * @Descripition TODO
 * @Author Administrator
 * @Date 2018/12/1 11:13
 **/
public class RedisReentrantLock {
    private Jedis jedis;

    private ThreadLocal<Map<String, Integer>> count=new ThreadLocal<>();

    public RedisReentrantLock(Jedis jedis) {
        this.jedis = jedis;
    }

    public boolean doLock(String key) {
        return jedis.set(key, "", "nx", "ex", 5l) != null;
    }

    public void doUnLock(String key) {
        jedis.del(key);
    }

    public Map<String, Integer> getCurrentLockMap() {
        Map<String, Integer> stringIntegerMap = count.get();
        if (stringIntegerMap != null) {
            return stringIntegerMap;
        }
        count.set(new HashMap<>());
        return count.get();
    }

    public boolean lock(String key) {
        Map<String, Integer> currentLockMap = getCurrentLockMap();
        Integer integer = currentLockMap.get(key);
        if (integer != null) {
            currentLockMap.put(key, integer + 1);
            return true;
        }
        boolean ok = this.doLock(key);
        if (!ok) {
            return false;
        }
        currentLockMap.put(key, 1);
        return ok;
    }

    public boolean unLock(String key) {
        Map<String, Integer> currentLockMap = getCurrentLockMap();
        Integer integer = currentLockMap.get(key);
        if (integer == null) {
            return false;
        }
        integer -= 1;
        if (integer > 0) {
            currentLockMap.put(key, integer);
        } else {
            currentLockMap.remove(key);
            this.doUnLock(key);
        }
        return true;
    }

    public static void main(String[] args) {
        JedisShardInfo jedisShardInfo = new JedisShardInfo("redis://localhost:6379");
        jedisShardInfo.setPassword("test");
        Jedis jedis = new Jedis(jedisShardInfo);
        RedisReentrantLock redisReentrantLock = new RedisReentrantLock(jedis);
        System.out.println(redisReentrantLock.lock("yrflovess"));
        System.out.println(redisReentrantLock.lock("yrflovess"));
        System.out.println(redisReentrantLock.unLock("yrflovess"));
        System.out.println(redisReentrantLock.unLock("yrflovess"));
    }
}

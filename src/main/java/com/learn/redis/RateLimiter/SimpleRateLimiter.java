package com.learn.redis.RateLimiter;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.io.IOException;

/**
 * @ClassName SimpleRateLimiter
 * @Descripition TODO
 * @Author Administrator
 * @Date 2018/12/1 16:41
 **/
public class SimpleRateLimiter {
    //    private Jedis jedis;
//
//    public SimpleRateLimiter(Jedis jedis) {
//        this.jedis = jedis;
//    }
//
//    public boolean doActionBoolean(String userId, String actionKey, int period, int maxCount){
//        String format = String.format("hist:%s:%s", userId, actionKey);
//        long currentTime = System.currentTimeMillis();
//        Pipeline pipelined = jedis.pipelined();
//        pipelined.multi();
//        pipelined.zadd(format, currentTime, "" + currentTime);
//        pipelined.zremrangeByScore(format, 0, currentTime - period * 1000);
//        Response<Long> zcard = pipelined.zcard(format);
////        pipelined.expire(format, period + 1);
//        pipelined.exec();
//        try {
//            pipelined.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return zcard.get()<=maxCount;
//    }
//
//    public static void main(String[] args) {
//        JedisShardInfo jedisShardInfo = new JedisShardInfo("redis://localhost:6379");
//        jedisShardInfo.setPassword("test");
//        Jedis jedis = new Jedis(jedisShardInfo);
//        SimpleRateLimiter limiter = new SimpleRateLimiter(jedis);
//        for(int i=0;i<20;i++) {
//            System.out.println(limiter.doActionBoolean("laoqian", "reply", 60, 5));
//        }
//    }
    private Jedis jedis;

    public SimpleRateLimiter(Jedis jedis) {
        this.jedis = jedis;
    }

    public boolean isActionAllowed(String userId, String actionKey, int period, int maxCount) {
        String key = String.format("hist:%s:%s", userId, actionKey);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long nowTs = System.currentTimeMillis();
        Pipeline pipe = jedis.pipelined();
        pipe.multi();
        pipe.zadd(key, nowTs, "" + nowTs);
        pipe.zremrangeByScore(key, 0, nowTs - period * 1000);
        Response<Long> count = pipe.zcard(key);
        pipe.expire(key, period + 1);
        pipe.exec();
        try {
            pipe.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count.get() <= maxCount;
    }

    public static void main(String[] args) {
        JedisShardInfo jedisShardInfo = new JedisShardInfo("redis://localhost:6379");
        jedisShardInfo.setPassword("test");
        Jedis jedis = new Jedis(jedisShardInfo);
        SimpleRateLimiter limiter = new SimpleRateLimiter(jedis);
        for (int i = 0; i < 20; i++) {
            if(limiter.isActionAllowed("laoqian", "reply", 60, 5)){
                System.out.println("用户可以操作");
            }else{
                System.out.println("拒绝访问");
            }
        }
    }
}

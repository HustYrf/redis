package com.learn.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

/**
 * @ClassName Chapter3
 * @Descripition TODO
 * @Author Administrator
 * @Date 2018/11/28 14:56
 **/
public class Chapter3 {
    public static void main(String[] args) {
        JedisShardInfo jedisShardInfo = new JedisShardInfo("redis://localhost:6379");
        jedisShardInfo.setPassword("test");
        Jedis conn = new Jedis(jedisShardInfo);
        conn.select(3);
        conn.incr("key");
        conn.incrBy("key",100);
        System.out.println(conn.get("key"));
        conn.hset("key2","key3","yrf");


        conn.sadd("redis-set","1","2","3","4");
//        String [] Strings = new String[2];
//        Strings[0]="1";
//        Strings[1]="2";
//        conn.srem("redis-set",Strings);
        System.out.println(conn.smembers("redis-set"));
        System.out.println(conn.scard("redis-set"));
    }
}

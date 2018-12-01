package com.learn.redis.HyperLogLog;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

/**
 * @ClassName HyperLogLogTest
 * @Descripition TODO
 * @Author Administrator
 * @Date 2018/12/1 14:36
 **/
public class HyperLogLogTest {
    public static void main(String[] args) {
        JedisShardInfo jedisShardInfo = new JedisShardInfo("redis://localhost:6379");
        jedisShardInfo.setPassword("test");
        Jedis jedis = new Jedis(jedisShardInfo);
        for (int i = 0; i < 100000; i++) {
            jedis.pfadd("HyperLogLog","test"+i);
        }
        for (int i = 90000; i < 120000; i++) {
            jedis.pfadd("HyperLogLog2","test"+i);
        }
        jedis.pfmerge("HyperLogLog3","HyperLogLog","HyperLogLog2");
        System.out.println(jedis.pfcount("HyperLogLog3"));
    }
}

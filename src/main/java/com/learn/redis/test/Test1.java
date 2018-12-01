package com.learn.redis.test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

/**
 * @ClassName Test1
 * @Descripition TODO
 * @Author Administrator
 * @Date 2018/11/29 13:38
 **/
public class Test1 {
    public static void main(String[] args) {
        JedisShardInfo jedisShardInfo = new JedisShardInfo("redis://localhost:6379");
        jedisShardInfo.setPassword("test");
        Jedis conn = new Jedis(jedisShardInfo);
        conn.select(5);
        EmailPojo emailPojo=new EmailPojo();
        emailPojo.setSellerId(17);
        emailPojo.setItemId("ItemM");
        emailPojo.setPrice(97);
        emailPojo.setBullerId(27);
        emailPojo.setTime(System.currentTimeMillis());
        String emailKey = "EMAIL:KEY";
        String emailInfo = GsonUtil.GsonString(emailPojo);
        Long rpush = conn.rpush(emailKey, emailInfo);
//        String rpop = conn.rpop(emailKey);
//        EmailPojo emailPojo1 = GsonUtil.GsonToBean(rpop, EmailPojo.class);
//        System.out.println(emailPojo1.getBullerId());
    }
}

package com.learn.redis.RedisDelayingQueue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.UUID;

/**
 * @ClassName RedisDelayingQueue
 * @Descripition TODO
 * @Author Administrator
 * @Date 2018/12/1 12:28
 **/
public class RedisDelayingQueue<T> {
    static class TaskItem<T> {
        public String id;
        public T msg;
    }

    // fastjson 序列化对象中存在 generic 类型时，需要使用 TypeReference
    private Type TaskType = new TypeReference<TaskItem<T>>() {
    }.getType();
    private Jedis jedis;
    private String queueKey;

    public RedisDelayingQueue(Jedis jedis, String queueKey) {
        this.jedis = jedis;
        this.queueKey = queueKey;
    }

    public void delayQueue(T msg) {
        TaskItem<T> tTaskItem = new TaskItem<>();
        tTaskItem.id = UUID.randomUUID().toString();
        tTaskItem.msg = msg;
        String s = JSON.toJSONString(tTaskItem);
        jedis.zadd(queueKey, System.currentTimeMillis() + 5000, s);
    }

    public void loop() {
        while (!Thread.interrupted()) {
            // 只取一条
            Set<String> values = jedis.zrangeByScore(queueKey, 0, System.currentTimeMillis(), 0, 1);
            if (values.isEmpty()) {
                try {
                    Thread.sleep(500); // 歇会继续
                } catch (InterruptedException e) {
                    break;
                }
                continue;
            }
            String s = values.iterator().next();
            if (jedis.zrem(queueKey, s) > 0) { // 抢到了
                TaskItem<T> task = JSON.parseObject(s, TaskType); // fastjson 反序列化
                this.handleMsg(task.msg);
            }
        }
    }

    public void handleMsg(T msg) {
        System.out.println(msg);
    }


    public static void main(String[] args) {
        JedisShardInfo jedisShardInfo = new JedisShardInfo("redis://localhost:6379");
        jedisShardInfo.setPassword("test");
        Jedis jedis = new Jedis(jedisShardInfo);
        RedisDelayingQueue<String> tRedisDelayingQueue = new RedisDelayingQueue<>(jedis, "q-demo");
        Thread producer = new Thread() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    tRedisDelayingQueue.delayQueue("codehole" + i);
                }
            }
        };
        Thread consumer = new Thread() {
            public void run() {
                tRedisDelayingQueue.loop();
            }
        };
        producer.start();
        consumer.start();
        try {
            producer.join();
            Thread.sleep(6000);
            consumer.interrupt();
            consumer.join();
        } catch (InterruptedException e) {
        }

    }

}

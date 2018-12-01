package com.learn.redis.test;

import lombok.Data;

/**
 * @ClassName EmailPojo
 * @Descripition TODO
 * @Author Administrator
 * @Date 2018/11/29 13:42
 **/
@Data
public class EmailPojo {
    private Integer sellerId;

    private String itemId;

    private int price;

    private Integer bullerId;

    private long time;
}

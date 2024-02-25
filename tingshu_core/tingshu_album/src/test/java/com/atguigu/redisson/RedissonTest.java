package com.atguigu.redisson;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedissonTest {
//    注入配置类
    @Autowired
    private RedissonClient redissonClient;
//    测试是否注入成功
    @Test
    public void testRedisson() {
        System.out.println(redissonClient);
    }
}

package com.atguigu.service.impl;

import com.atguigu.service.SetNumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
public class SetNumServiceImpl implements SetNumService {

    //    引入redisTemple
    @Autowired
    private RedisTemplate redisTemplate;
/*
       单个服务器
    @Override
       public synchronized void  setNum() {
   //        获取值
           String num = (String) redisTemplate.opsForValue().get("num");
   //        判断
           if (num == null) {
               redisTemplate.opsForValue().set("num", "1");
           } else {
               //        设置值+1
               redisTemplate.opsForValue().set("num", String.valueOf(Integer.parseInt(num) + 1));
           }
       }
       @Override
       public synchronized void  setNum() {
   //        利用redis的setNx命令,给锁一个指定时间,超时就失效
           Boolean accquireLock = redisTemplate.opsForValue().setIfAbsent("lock", "ok", 3, TimeUnit.SECONDS);
   //        判断是否获取到锁
           if (accquireLock) {
               doBusiness();
               redisTemplate.delete("lock");
           } else {
               //        未获取到锁,等待100ms
               try {
                   Thread.sleep(100);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
               //        递归调用,继续获取锁
               setNum();
           }
       }
   */

//    @Override
//    public synchronized void  setNum() {
//        //设置一个uuid标识
//        String token = UUID.randomUUID().toString();
//
////        利用redis的setNx命令,给锁一个指定时间,超时就失效
//        Boolean accquireLock = redisTemplate.opsForValue().setIfAbsent("lock", token, 3, TimeUnit.SECONDS);
////        判断是否获取到锁
//        if (accquireLock) {
//            doBusiness();
////            获取值
//            String redisToken = (String) redisTemplate.opsForValue().get("lock");
////            判断是否是自己的锁
//            if (token.equals(redisToken)) {
//                redisTemplate.delete("lock");
//            }
//        } else {
//            //        未获取到锁,等待100ms
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            //        递归调用,继续获取锁
//            setNum();
//        }
//    }

//    @Override
//    public synchronized void  setNum() {
//        //设置一个uuid标识
//        String token = UUID.randomUUID().toString();
//
////        利用redis的setNx命令,给锁一个指定时间,超时就失效
//        Boolean accquireLock = redisTemplate.opsForValue().setIfAbsent("lock", token, 3, TimeUnit.SECONDS);
////        判断是否获取到锁
//        if (accquireLock) {
//            doBusiness();
////            获取值
//            String redisToken = (String) redisTemplate.opsForValue().get("lock");
////            判断是否是自己的锁
////            使用lua脚本
//            String lua = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
////            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(lua, Long.class);
////            redisScript.setScriptText(lua);
////            redisScript.setResultType(Long.class);
////            redisTemplate.execute(redisScript, Arrays.asList("lock"), token);
//            redisTemplate.execute(new DefaultRedisScript<>(lua, Long.class), Collections.singletonList("lock"), token);
////            if (token.equals(redisToken)) {
////                redisTemplate.delete("lock");
////            }
//        } else {
//            //        未获取到锁,等待100ms
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            //        递归调用,继续获取锁
//            setNum();
//        }
//    }

    //优化反复调用获取锁的代码,解决栈溢出的问题
//    @Override
//    public synchronized void  setNum() {
//        //设置一个uuid标识
//        String token = UUID.randomUUID().toString();
//
////        利用redis的setNx命令,给锁一个指定时间,超时就失效
//        Boolean accquireLock = redisTemplate.opsForValue().setIfAbsent("lock", token, 3, TimeUnit.SECONDS);
////        判断是否获取到锁
//        if (accquireLock) {
//            doBusiness();
////            判断是否是自己的锁
////            使用lua脚本
//            String lua = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//            redisTemplate.execute(new DefaultRedisScript<>(lua, Long.class), Collections.singletonList("lock"), token);
//        } else {
//            //        未获取到锁,等待100ms
//            try {
//                while (true) {
//                    Thread.sleep(100);
//                    //        重试获取锁
//                    Boolean retryAccquireLock = redisTemplate.opsForValue().setIfAbsent("lock", token, 3, TimeUnit.SECONDS);
//                    //        判断是否获取到锁,拿到了就终止循环
//                    if (retryAccquireLock) {
//                        break;
//                    }
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            //        递归调用,继续获取锁
//            setNum();
//        }
//    }

    //        解决锁不可重入的问题
    ThreadLocal<String> threadLocal = new ThreadLocal<>();
    @Override
    public synchronized void setNum() {
        boolean accquireLock = false;
//        当下一个线程来的时候,获取map中线程
        String token = threadLocal.get();
        //  判断是否是自己的锁
        if (StringUtils.isEmpty(token)) {
            accquireLock = true;
        } else {
            //设置一个uuid标识
            token = UUID.randomUUID().toString();
//        利用redis的setNx命令,给锁一个指定时间,超时就失效
            accquireLock = redisTemplate.opsForValue().setIfAbsent("lock", token, 3, TimeUnit.SECONDS);
        }
//        判断是否获取到锁
        if (accquireLock) {
            doBusiness();
//            判断是否是自己的锁
//            使用lua脚本
            String lua = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            redisTemplate.execute(new DefaultRedisScript<>(lua, Long.class), Collections.singletonList("lock"), token);

            threadLocal.remove();
        } else {
            //        未获取到锁,等待100ms
            try {
                while (true) {
                    Thread.sleep(100);
                    //        重试获取锁
                    Boolean retryAccquireLock = redisTemplate.opsForValue().setIfAbsent("lock", token, 3, TimeUnit.SECONDS);
                    //        判断是否获取到锁,拿到了就终止循环
                    if (retryAccquireLock) {
                        //        将线程放入map中
                        threadLocal.set(token);
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //        递归调用,继续获取锁
            setNum();
        }
    }


    private void doBusiness() {
        //        获取值
        String num = (String) redisTemplate.opsForValue().get("num");
//        判断
        if (num == null) {
            redisTemplate.opsForValue().set("num", "1");
        } else {
            //        设置值+1
            redisTemplate.opsForValue().set("num", String.valueOf(Integer.parseInt(num) + 1));
        }
    }
}

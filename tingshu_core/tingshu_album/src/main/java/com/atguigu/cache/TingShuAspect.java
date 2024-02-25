package com.atguigu.cache;

import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


@Component
@Aspect
public class TingShuAspect {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    ThreadLocal<String> threadLocal = new ThreadLocal<>();

    //   最终优化 切面编程 + 本地锁 ,不用分布式锁
    @SneakyThrows
    @Around("@annotation(com.atguigu.cache.TingShuCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint joinPoint) {
        //1.获取目标方法上面的参数
        Object[] methodParams = joinPoint.getArgs();
        //2.拿到目标方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = signature.getMethod();
        //3.拿到目标方法上面的注解
        TingShuCache tingShuCache = targetMethod.getAnnotation(TingShuCache.class);
        //4.拿到注解上面的值
        String prefix = tingShuCache.value();
        Object firstParam = methodParams[0];
        String cacheKey = prefix + ":" + firstParam;
        Object redisObject = redisTemplate.opsForValue().get(cacheKey);
        String lockKey = "lock-" + firstParam;
        //        如果缓存中没有数据,那么说明需要查询数据库
        if (redisObject == null) {
            //使用本地锁
            synchronized (lockKey.intern()) {
                if (redisObject == null) {
                    Object objectDb = joinPoint.proceed();
                    redisTemplate.opsForValue().set(cacheKey, objectDb);
                    return objectDb;
                }
            }
        }
        return redisObject;
    }

//    //    代码再优化使用 切面编程 + 读写锁 ,因为切面里面的业务只负责查询,读读互不干扰,所以干脆就不用分布式锁
//    @SneakyThrows
//    @Around("@annotation(com.atguigu.cache.TingShuCache)")
//    public Object cacheAroundAdvice(ProceedingJoinPoint joinPoint) {
//        //1.获取目标方法上面的参数
//        Object[] methodParams = joinPoint.getArgs();
//        //2.拿到目标方法
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        Method targetMethod = signature.getMethod();
//        //3.拿到目标方法上面的注解
//        TingShuCache tingShuCache = targetMethod.getAnnotation(TingShuCache.class);
//        //4.拿到注解上面的值
//        String prefix = tingShuCache.value();
//        Object firstParam = methodParams[0];
//        String cacheKey = prefix + ":" + firstParam;
//        Object redisObject = redisTemplate.opsForValue().get(cacheKey);
//        String lockKey = "lock-" + firstParam;
//        RReadWriteLock rwLock = redissonClient.getReadWriteLock(lockKey);
//        try {
//            //        如果缓存中没有数据,那么说明需要查询数据库
//            if (redisObject == null) {
//                //        获取锁
//                rwLock.readLock().lock();
//    //            读取数据库中的数据
//                redisObject = redisTemplate.opsForValue().get(cacheKey);
//                rwLock.readLock().unlock();
//                //       拿到锁之后,使用切面做业务,把数据存入缓存返回,最后释放锁
//                    //                添加写锁
//                    rwLock.writeLock().lock();
//                    Object objectDb = joinPoint.proceed();
//
//                    redisTemplate.opsForValue().set(cacheKey, objectDb);
//                    rwLock.writeLock().unlock();
//                return objectDb;
//            }
//        } finally {
//            rwLock.readLock().unlock();
//        }
//        return redisObject;
//    }


////    代码优化使用 切面编程 + redisson = 分布式锁
//    @SneakyThrows
//    @Around("@annotation(com.atguigu.cache.TingShuCache)")
//    public Object cacheAroundAdvice(ProceedingJoinPoint joinPoint) {
//        //1.获取目标方法上面的参数
//        Object[] methodParams = joinPoint.getArgs();
//        //2.拿到目标方法
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        Method targetMethod = signature.getMethod();
//        //3.拿到目标方法上面的注解
//        TingShuCache tingShuCache = targetMethod.getAnnotation(TingShuCache.class);
//        //4.拿到注解上面的值
//        String prefix = tingShuCache.value();
//        Object firstParam = methodParams[0];
//        String cacheKey = prefix + ":" + firstParam;
//        Object redisObject = redisTemplate.opsForValue().get(cacheKey);
//        String lockKey = "lock-" + firstParam;
//        //        如果缓存中没有数据,那么说明需要查询数据库
//        if (redisObject == null) {
//            //        获取锁
//            RLock lock = redissonClient.getLock(lockKey);
//            lock.lock();
//            //       拿到锁之后,使用切面做业务,把数据存入缓存返回,最后释放锁
//            try {
//                Object objectDb = joinPoint.proceed();
//                redisTemplate.opsForValue().set(cacheKey, objectDb);
//                return objectDb;
//            } finally {
//                lock.unlock();
//            }
//        }
//        return redisObject;
//    }


//   使用 切面编程 + redis = 分布式锁
//    @SneakyThrows
//    @Around("@annotation(com.atguigu.cache.TingShuCache)")
//    public Object cacheAroundAdvice(ProceedingJoinPoint joinPoint) {
//        //1.获取目标方法上面的参数
//        Object[] methodParams = joinPoint.getArgs();
//        //2.拿到目标方法
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        Method targetMethod = signature.getMethod();
//        //3.拿到目标方法上面的注解
//        TingShuCache tingShuCache = targetMethod.getAnnotation(TingShuCache.class);
//        //4.拿到注解上面的值
//        String prefix = tingShuCache.value();
//        Object firstParam = methodParams[0];
//        String cacheKey = prefix + ":" + firstParam;
//        Object redisObject = redisTemplate.opsForValue().get(cacheKey);
//        String lockKey = "lock-" + firstParam;
//        if (redisObject == null) {
//            boolean accquireLock = false;
//            String token = threadLocal.get();
//            //判断线程中的token是否为空,不为空说明是自己的锁,
//            if (!StringUtils.isEmpty(token)) {
//                accquireLock = true;
//            } else {
//                //还有很多代码要执行 1000行代码
//                token = UUID.randomUUID().toString();
//                accquireLock = redisTemplate.opsForValue().setIfAbsent(lockKey, token, 3, TimeUnit.SECONDS);
//            }
//            // 是自己的锁就执行下面的代码
//            if (accquireLock) {
//                Object objectDb = joinPoint.proceed();
//                redisTemplate.opsForValue().set(cacheKey, objectDb);
//                String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
//                redisScript.setScriptText(luaScript);
//                redisScript.setResultType(Long.class);
//                redisTemplate.execute(redisScript, Arrays.asList(lockKey), token);
//                //擦屁股--风干--水洗
//                threadLocal.remove();
//                return objectDb;
//            } else {
//                //目的是为了拿到锁 自旋
//                while (true) {
//                    SleepUtils.millis(50);
//                    boolean retryAccquireLock = redisTemplate.opsForValue().setIfAbsent(lockKey, token, 3, TimeUnit.SECONDS);
//                    if (retryAccquireLock) {
//                        threadLocal.set(token);
//                        break;
//                    }
//                }
//                return cacheAroundAdvice(joinPoint);
//            }
//        }
//        return redisObject;
//    }
}

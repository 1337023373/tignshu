package com.atguigu.login;

import com.atguigu.constant.RedisConstant;
import com.atguigu.entity.UserInfo;
import com.atguigu.execption.GuiguException;
import com.atguigu.result.ResultCodeEnum;
import com.atguigu.util.AuthContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
public class LoginAspect {
    @Autowired
    private RedisTemplate redisTemplate;

    //    事务设置为环绕通知,以后方法头上有@TingShuLogin的
    @Around("@annotation(com.atguigu.login.TingShuLogin)")
    public void process(ProceedingJoinPoint joinPoint) throws Throwable {
        //拿到请求里面的token信息
//        因为请求在小程序中，跟以前的从前端浏览器拿数据的httpServletRequest不一样，
//        RequestContextHolder表示当前容器的上下文，拿到当前线程中的一些属性
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

//        不转型，attributes类是无法方便操作request、session这些原生servlet相关的对象或者属性的，因为本身Java Web最原始的实现就是servlet形式的，Spring框架当然会为其做特定的一些封装，也就是这个类的来源。
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String token = request.getHeader("token");

        //拿到目标方法上面的注解
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method targetMethod = methodSignature.getMethod();
        TingShuLogin tingShuLogin = targetMethod.getAnnotation(TingShuLogin.class);

//        拿到后通过设定你的requried（）是否为true进行判断
        if (tingShuLogin.required()) {
//            如果token为空则说明没有登录，需要返回208，进行弹窗登录
            if (StringUtils.isEmpty(token)) {
                throw new GuiguException(ResultCodeEnum.UN_LOGIN);
            }
//            有token，但是已经过期，那同过token拿到的userinfo信息就是空
            UserInfo userInfo = (UserInfo) redisTemplate.opsForValue().get(RedisConstant.RANKING_KEY_PREFIX);
            if (userInfo == null) {
                throw new GuiguException(ResultCodeEnum.UN_LOGIN);
            }
        }
//        不为空就代表已经登录
        if (!StringUtils.isEmpty(token)) {
            UserInfo userInfo = (UserInfo) redisTemplate.opsForValue().get(RedisConstant.RANKING_KEY_PREFIX);
            if (userInfo != null) {
//                就把登录的基本信息保存起来
                AuthContextHolder.setUserId(userInfo.getId());
                AuthContextHolder.setUsername(userInfo.getNickname());

            }
//        执行目标方法
            joinPoint.proceed();
        }
    }
}

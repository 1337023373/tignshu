package com.atguigu.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignInterceptor implements RequestInterceptor {

    public void apply(RequestTemplate requestTemplate){
        /**
         * 说明：异步编排 与 MQ消费者端 时RequestContextHolder.getRequestAttributes()为null
         * 异步编排原因：request 信息是存储在 ThreadLocal 中的，所以子线程根本无法获取到主线程的  request 信息。
         * 异步编排解决方案：
         *  1、启动类添加@EnableAsync注解
         *  2、在新开子线程之前，将RequestAttributes对象设置为子线程共享，代码如下：
         *      ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
         *      RequestContextHolder.setRequestAttributes(sra, true);
         *
         *  MQ消费者端Feign调用时需注意参数传递
         */
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //异步编排 与 MQ消费者端 为 null
        if(null != requestAttributes) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)requestAttributes;
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String token = request.getHeader("token");
            requestTemplate.header("token", token);
        }
    }
}
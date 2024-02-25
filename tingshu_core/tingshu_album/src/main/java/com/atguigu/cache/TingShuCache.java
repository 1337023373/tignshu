package com.atguigu.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//表示注解的作用范围
@Target({ElementType.METHOD, ElementType.TYPE})
//表示注解的生命周期
@Retention(RetentionPolicy.RUNTIME)
public @interface TingShuCache {
    //表示注解的属性
    String value() default "cache";
}

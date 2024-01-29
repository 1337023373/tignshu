package com.atguigu.login;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author H-H sama
 */

//@interface代表注释

@Target(ElementType.METHOD)
//接口注释的作用范围在方法上
@Retention(RetentionPolicy.RUNTIME)
//生命周期，在程序运行期间都会存在
public @interface TingShuLogin {
//    默认是否需要登录
    boolean required() default true;
}

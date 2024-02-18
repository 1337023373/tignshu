package com.atguigu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
//类上 加注解@Component ，才能被spring加入 IOC 容器，成为 bean。。对象存在，才能映射
@Component
//可以用 Spring 自带的 @Value 注解外，SpringBoot 还提供了一种更加方便的方式：@ConfigurationProperties。
// 只要在 Bean 上添加上了这个注解，指定好配置文件的前缀，那么对应的配置文件数据就会自动填充到 Bean 中。
//拿到配置文件中的属性值
@ConfigurationProperties(prefix = "wechat.login")
//外部引入的  第三方模块【与启动类，不在一个包】。且【不知道 导入那个包】，该如何 配置
//   这样，就 引入了 @Enable** 这种形式 ，其底层 是 封装的 @Import 注解
public class WeChatProperties {
    private String appId;
    private String appSecret;
}

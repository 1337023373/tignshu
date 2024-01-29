package com.atguigu.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class WeChatLoginConfig {
    @Autowired
    private WeChatProperties weChatProperties;
    @Bean
    public WxMaService wxMaService() {

        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
//        id和密钥从配置类中获取，把设置在yml中的属性拿出来
        config.setAppid(weChatProperties.getAppId());
        config.setSecret(weChatProperties.getAppSecret());
        config.setMsgDataFormat("JSON");
//        WxMaServiceImpl可以向上追溯到父类的BaseWxMaServiceImpl，里面有需要的类型
        WxMaServiceImpl service = new WxMaServiceImpl();
        service.setWxMaConfig(config);

        return service;
    }
}

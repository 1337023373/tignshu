package com.atguigu.threadpool;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "thread.pool")
public class MythreadPoolProperties {
//    这里需要初始化赋值,因为这个配置类是共享的,其他的模块读取到,
//    但是因为考虑的其他模块暂时不会使用线程池,就需要去对应的配置文件中
//    添加线程池的设置,所以这里给一个初始的值,避免出现空指针异常
    public Integer corePoolSize=16;
    public Integer maximumPoolSize=32;
    public Integer keepAliveTime=50;
    public Integer queueLength=100;
}

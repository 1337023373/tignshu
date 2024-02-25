package com.atguigu.threadpool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(MythreadPoolProperties.class)
public class MyThreadPool {
    @Autowired
    private MythreadPoolProperties mythreadPoolProperties;

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
      return  new ThreadPoolExecutor(mythreadPoolProperties.getCorePoolSize(),
                mythreadPoolProperties.getMaximumPoolSize(),
                mythreadPoolProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(mythreadPoolProperties.getQueueLength()),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}

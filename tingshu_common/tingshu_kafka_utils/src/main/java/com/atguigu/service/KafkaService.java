package com.atguigu.service;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class KafkaService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 发送消息
     * @param topic
     * @param value
     * @return
     */
    public boolean sendMessage(String topic, Object value) {
        return this.sendMessage(topic, null, value);
    }
    /**
     * 发送消息
     * @param topic
     * @param key
     * @param value
     * @return
     */
    public boolean sendMessage(String topic, String key, Object value) {
        CompletableFuture<SendResult<String, Object>> completableFuture = kafkaTemplate.send(topic, key, value);
        //执行成功回调
        completableFuture.thenAccept(result -> {
            //System.out.println("发送成功:{}" + JSON.toJSONString(result));
            logger.debug("kafka发送消息成功: topic={}, key={}, value={}", topic, key, JSON.toJSONString(value));
        });
        //执行失败回调
        completableFuture.exceptionally(e -> {
            //发送失败，记录日志或者采取重发策略
            logger.error("kafka发送消息失败: topic={}, key={}, value={}", topic, key, JSON.toJSONString(value));
            e.printStackTrace();
            return null;
        });
        return true;
    }


    /**
     * 延迟消息
     * @param topic
     * @param value
     * @param delayTime 延迟的时间，单位：秒
     * @return
     */
    public boolean sendDelayMessage(String topic, Object value, int delayTime) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> kafkaTemplate.send(topic, value), delayTime, TimeUnit.SECONDS);
        executor.shutdown();
        return false;
    }

}

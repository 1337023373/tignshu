package com.atguigu.consumer;

import com.atguigu.constant.KafkaConstant;
import com.atguigu.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SearchConsumer {

    @Autowired
    private SearchService searchService;
//    设置kafka在消费者端的代码
    // 专辑上架
    @KafkaListener(topics = KafkaConstant.ONSALE_ALBUM_QUEUE)
    public void onSaleAlbum(Long albumId) {
//        判断albumId是否为空
        if (albumId == null) {
            searchService.onSaleAlbum(albumId);
        }
    }
//    专辑下架
    @KafkaListener(topics = KafkaConstant.OFFSALE_ALBUM_QUEUE)
    public void offSaleAlbum(Long albumId) {
        if (albumId == null) {
            searchService.offSaleAlbum(albumId);
        }
    }
}

package com.atguigu.consumer;

import com.alibaba.fastjson.JSON;
import com.atguigu.constant.KafkaConstant;
import com.atguigu.constant.SystemConstant;
import com.atguigu.entity.AlbumStat;
import com.atguigu.entity.TrackStat;
import com.atguigu.service.AlbumStatService;
import com.atguigu.service.TrackStatService;
import com.atguigu.vo.AlbumStatMqVo;
import com.atguigu.vo.TrackStatMqVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class AlbumConsumer {
    @Autowired
    private TrackStatService trackStatService;
    @Autowired
    private AlbumStatService albumStatService;
    @Autowired
    private RedisTemplate redisTemplate;

    //    写消费者
//    消费者监听的是更新声音统计的队列
    @KafkaListener(topics = KafkaConstant.UPDATE_TRACK_STAT_QUEUE)
    public void updateTrackStat(String dataJson) {
        TrackStatMqVo trackStatMqVo = JSON.parseObject(dataJson, TrackStatMqVo.class);
        String key = trackStatMqVo.getBusinessNo();
        //防止消息重复消费
        Boolean isExist = redisTemplate.opsForValue().setIfAbsent(key, 1, 20, TimeUnit.SECONDS);
        if (isExist) {
            //更新声音的数量
            String statType = trackStatMqVo.getStatType();
            LambdaQueryWrapper<TrackStat> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TrackStat::getTrackId, trackStatMqVo.getTarckId());
            wrapper.eq(TrackStat::getStatType, statType);
            TrackStat trackStat = trackStatService.getOne(wrapper);
            trackStat.setStatNum(trackStat.getStatNum() + trackStatMqVo.getCount());
            trackStatService.updateById(trackStat);
            if (statType.equals(SystemConstant.PLAY_NUM_TRACK)) {
                //更新专辑播放量
                LambdaQueryWrapper<AlbumStat> albumWrapper = new LambdaQueryWrapper<>();
                albumWrapper.eq(AlbumStat::getAlbumId, trackStatMqVo.getAlbumId());
                albumWrapper.eq(AlbumStat::getStatType, SystemConstant.PLAY_NUM_ALBUM);
                AlbumStat albumStat = albumStatService.getOne(albumWrapper);
                albumStat.setStatNum(albumStat.getStatNum() + trackStatMqVo.getCount());
                albumStatService.updateById(albumStat);
            }
//            更新es中的数据
        }
    }

    @KafkaListener(topics = KafkaConstant.UPDATE_ALBUM_BUY_NUM_QUEUE)
    public void updateAlbumStat(String dataJson) {
        AlbumStatMqVo albumStatMqVo = JSON.parseObject(dataJson, AlbumStatMqVo.class);
        String kbusinessNoy = albumStatMqVo.getBusinessNo();
        //防止消息重复消费
        Boolean isExist = redisTemplate.opsForValue().setIfAbsent(kbusinessNoy, 1, 20, TimeUnit.SECONDS);
        if (isExist) {
//         更新专辑数量
            LambdaQueryWrapper<AlbumStat> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AlbumStat::getAlbumId, albumStatMqVo.getAlbumId());
            wrapper.eq(AlbumStat::getStatType, albumStatMqVo.getStatType());
            AlbumStat albumStat = albumStatService.getOne(wrapper);
            albumStat.setStatNum(albumStat.getStatNum() + 1);
            albumStatService.updateById(albumStat);
        }
    }
}

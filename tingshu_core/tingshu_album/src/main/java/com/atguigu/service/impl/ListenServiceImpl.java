package com.atguigu.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.constant.KafkaConstant;
import com.atguigu.constant.SystemConstant;
import com.atguigu.entity.UserCollect;
import com.atguigu.entity.UserListenProcess;
import com.atguigu.service.KafkaService;
import com.atguigu.service.ListenService;
import com.atguigu.service.TrackInfoService;
import com.atguigu.util.AuthContextHolder;
import com.atguigu.util.MongoUtil;
import com.atguigu.vo.TrackStatMqVo;
import com.atguigu.vo.TrackTempVo;
import com.atguigu.vo.UserCollectVo;
import com.atguigu.vo.UserListenProcessVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ListenServiceImpl implements ListenService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private KafkaService kafkaService;
    @Autowired
    private TrackInfoService trackInfoService;


    @Override
    public void updatePlaySecond(UserListenProcessVo userListenProcessVo) {
        Long userId = AuthContextHolder.getUserId();
//        需要更新播放进度,首先要查询是否有播放进度,如果有,则更新,如果没有,则插入
//        这个代码的意思是,mongodb拿到表中userid字段的数据与得到的userid相同的数据且trackid字段也与传入的对象的trackid相同的数据,也就是说查询这个播放进度,需要用户id和声音id都指向同一个才能获取,不然就是其他人的声音消息
        Query query =  Query.query(Criteria.where("userId").is(userId).and("trackId").is(userListenProcessVo.getTrackId()));
        UserListenProcess userListenProcess = mongoTemplate.findOne(query, UserListenProcess.class,
                MongoUtil.getCollectionName(MongoUtil.MongoCollectionEnum.USER_LISTEN_PROCESS, userId));

//        判断是否有播放进度,没有就添加播放进度
        if (userListenProcess == null) {
//        把播放进度存放在mongodb中
            userListenProcess = new UserListenProcess();
//        把vo对象的属性复制到userListenProcess中,使用BeanUtils.copyProperties(userListenProcessVo,userListenProcess)也可以;
            userListenProcess.setAlbumId(userListenProcessVo.getAlbumId());
            userListenProcess.setTrackId(userListenProcessVo.getTrackId());
            userListenProcess.setBreakSecond(userListenProcessVo.getBreakSecond());
//        把其他信息也存放到userListenProcess中

            userListenProcess.setUserId(userId);
            userListenProcess.setCreateTime(new Date());
            userListenProcess.setUpdateTime(new Date());
            userListenProcess.setIsShow(1);
            userListenProcess.setId(ObjectId.get().toString());
//        保存到mongodb中的集合中
            mongoTemplate.save(userListenProcess, MongoUtil.getCollectionName(MongoUtil.MongoCollectionEnum.USER_LISTEN_PROCESS, userId));
        }else {
//           有就 更新mongoDB播放进度
            userListenProcess.setBreakSecond(userListenProcessVo.getBreakSecond());
            userListenProcess.setUpdateTime(new Date());
            mongoTemplate.save(userListenProcess, MongoUtil.getCollectionName(MongoUtil.MongoCollectionEnum.USER_LISTEN_PROCESS, userId));
        }
//      更新播放次数,默认同一个声音同一天同一个人多次播放,只加1
        //4.更新播放次数  同一个专辑同一个声音同一个客户每天只能播放数量加1
        String key = "user:track:" + new DateTime().toString("yyyyMMdd") + ":" + userListenProcessVo.getTrackId();
        boolean isExist = redisTemplate.opsForValue().getBit(key, userId);
        if (!isExist) {
            redisTemplate.opsForValue().setBit(key, userId, true);
            //设置一个过期时间
            redisTemplate.expire(key, 1, TimeUnit.DAYS);
            //如果直接调方法 需要等待更新播放量方法才结束
            TrackStatMqVo trackStatVo = new TrackStatMqVo();
            trackStatVo.setBusinessNo(UUID.randomUUID().toString().replaceAll("-", ""));
            trackStatVo.setAlbumId(userListenProcessVo.getAlbumId());
            trackStatVo.setTarckId(userListenProcessVo.getTrackId());
            trackStatVo.setStatType(SystemConstant.PLAY_NUM_TRACK);
            trackStatVo.setCount(1);
            kafkaService.sendMessage(KafkaConstant.UPDATE_TRACK_STAT_QUEUE, JSON.toJSONString(trackStatVo));
        }
    }

    /**
     * 自动追踪上一次进度
     *
     * @param trackId
     * @return
     */
    @Override
    public BigDecimal getLastPlaySecond(Long trackId) {
//        从mongoDb中查询播放进度
        Long userId = AuthContextHolder.getUserId();
        Query query =  Query.query(Criteria.where("userId").is(userId).and("trackId").is(trackId));
        UserListenProcess userListenProcess = mongoTemplate.findOne(query, UserListenProcess.class,
                MongoUtil.getCollectionName(MongoUtil.MongoCollectionEnum.USER_LISTEN_PROCESS, userId));
//      判断表中是否有播放进度,没有就不做任何事情,有就返回播放进度
        if (userListenProcess != null) {
           return userListenProcess.getBreakSecond();
        }
        return new BigDecimal(0);
    }

    /**
     * 获取最近播放记录
     *
     * @return
     */
    @Override
    public HashMap<String, Object> getRecentlyPlay() {
//        从mongoDb中查询
        Long userId = AuthContextHolder.getUserId();
        Query query = Query.query(Criteria.where("userId").is(userId));
        Sort sort = Sort.by(Sort.Direction.DESC, "updateTime");
        query.with(sort);
        UserListenProcess userListenProcess = mongoTemplate.findOne(query, UserListenProcess.class,
                MongoUtil.getCollectionName(MongoUtil.MongoCollectionEnum.USER_LISTEN_PROCESS, userId));
        if (userListenProcess == null) {
//            说明没有播放记录
            return null;
        };
//        否则把查询到的albumId和trackid返回
        HashMap<String, Object> map = new HashMap<>();
        map.put("albumId", userListenProcess.getAlbumId());
        map.put("trackId", userListenProcess.getTrackId());
        return map;
    }

    /**
     * 点击收藏按钮,收藏声音
      * @param trackId
     * @return
     */
    @Override
    public boolean collectTrack(Long trackId) {
        UserCollect userCollect = new UserCollect();
        Long userId = AuthContextHolder.getUserId();
//        从表中查询数据,没有就添加收藏进入,有就取消受收藏
        Query query = Query.query(Criteria.where("userId").is(userId).and("trackId").is(trackId));
        long count = mongoTemplate.count(query, MongoUtil.getCollectionName(MongoUtil.MongoCollectionEnum.USER_COLLECT, userId));
//            判断mongo表中是否存在数据
        if (count == 0) {
            //在mongodb中添加用户收藏的信息
            userCollect.setId(ObjectId.get().toString());
            userCollect.setUserId(userId);
            userCollect.setTrackId(trackId);
            userCollect.setCreateTime(new Date());
            mongoTemplate.save(userCollect, MongoUtil.getCollectionName(
                    MongoUtil.MongoCollectionEnum.USER_COLLECT, userId));
//            使用kafka更新声音的收藏量
            TrackStatMqVo trackStatMqVo = new TrackStatMqVo();
            trackStatMqVo.setBusinessNo(UUID.randomUUID().toString().replaceAll("-",""));
            trackStatMqVo.setTarckId(trackId);
            trackStatMqVo.setStatType(SystemConstant.COLLECT_NUM_TRACK);
            trackStatMqVo.setCount(-1);
            kafkaService.sendMessage(KafkaConstant.UPDATE_TRACK_STAT_QUEUE, JSON.toJSONString(trackStatMqVo));
            return true;
        }else {
            mongoTemplate.remove(query, MongoUtil.getCollectionName(MongoUtil.MongoCollectionEnum.USER_COLLECT, userId));
            //发送消息，更新声音统计数量减1
            TrackStatMqVo trackStatMqVo = new TrackStatMqVo();
            trackStatMqVo.setBusinessNo(UUID.randomUUID().toString().replaceAll("-",""));
            trackStatMqVo.setTarckId(trackId);
            trackStatMqVo.setStatType(SystemConstant.COLLECT_NUM_TRACK);
            trackStatMqVo.setCount(-1);
            kafkaService.sendMessage(KafkaConstant.UPDATE_TRACK_STAT_QUEUE, JSON.toJSONString(trackStatMqVo));
            return false;
        }
    }

    /**
     * 查询是否收藏声音
     * @param trackId
     * @return
     */
    @Override
    public boolean isCollected(Long trackId) {
        Long userId = AuthContextHolder.getUserId();
        Query query = Query.query(Criteria.where("userId").is(userId).and("trackId").is(trackId));
        long count = mongoTemplate.count(query,MongoUtil.getCollectionName(MongoUtil.MongoCollectionEnum.USER_COLLECT, userId));
        if (count > 0) return true;
        return false;
    }

    /**
     * 分页获取用户收藏声音列表
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page getUserCollectByPage(Integer page, Integer pageSize) {
//        获取当前id
        Long userId = AuthContextHolder.getUserId();
//  通过mongoDb去查找
        Query query = Query.query(Criteria.where("userId").is(userId));
//        分页,一定要-1
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        query.with(pageable);
//        排序，根据创建时间倒序排
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        query.with(sort);
//        从mongoDb中查找
        List<UserCollect> userCollectList = mongoTemplate.find(query, UserCollect.class,
                MongoUtil.getCollectionName(MongoUtil.MongoCollectionEnum.USER_COLLECT, userId));
//        获取总数
        long total = mongoTemplate.count(query.limit(-1).skip(-1L),
                MongoUtil.getCollectionName(MongoUtil.MongoCollectionEnum.USER_COLLECT, userId));
//       遍历列表,通过trackid获取,声音id列表
        List<Long> trackIdList = userCollectList.stream().map(UserCollect::getUserId).collect(Collectors.toList());
        List<UserCollectVo> userCollectVoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(trackIdList)) {
//            根据声音id列表获取声音信息列表
            List<TrackTempVo> trackVoList = trackInfoService.getTrackVoList(trackIdList);

            Map<Long, TrackTempVo> trackTempVoMap = trackVoList.stream().
                    collect(Collectors.toMap(TrackTempVo::getTrackId, trackTempVo -> trackTempVo));

//            对查询到的用户收藏列表进行迭代
           userCollectVoList = userCollectList.stream().map(userCollect -> {
                UserCollectVo userCollectVo = new UserCollectVo();
                Long trackId = userCollect.getTrackId();
//                通过声音id拿到对应的声音详情信息
                TrackTempVo trackTempVo = trackTempVoMap.get(trackId);
                BeanUtils.copyProperties( trackTempVo,userCollect);
                userCollectVo.setTrackId(trackId);
                userCollectVo.setCreateTime(userCollect.getCreateTime());
                return userCollectVo;
            }).collect(Collectors.toList());
        }
        return new Page(page, pageSize, total).setRecords(userCollectVoList);
    }
}

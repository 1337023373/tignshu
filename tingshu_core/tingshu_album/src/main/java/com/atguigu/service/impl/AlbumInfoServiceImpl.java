package com.atguigu.service.impl;

import com.atguigu.SearchFeignClient;
import com.atguigu.cache.TingShuCache;
import com.atguigu.constant.KafkaConstant;
import com.atguigu.constant.RedisConstant;
import com.atguigu.constant.SystemConstant;
import com.atguigu.entity.AlbumAttributeValue;
import com.atguigu.entity.AlbumInfo;
import com.atguigu.entity.AlbumStat;
import com.atguigu.mapper.AlbumInfoMapper;
import com.atguigu.service.AlbumAttributeValueService;
import com.atguigu.service.AlbumInfoService;
import com.atguigu.service.AlbumStatService;
import com.atguigu.service.KafkaService;
import com.atguigu.util.AuthContextHolder;
import com.atguigu.util.MongoUtil;
import com.atguigu.util.SleepUtils;
import com.atguigu.vo.AlbumTempVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 专辑信息 服务实现类
 * </p>
 *
 * @author 亨亨sama
 * @since 2024-01-29
 */
@Service
public class AlbumInfoServiceImpl extends ServiceImpl<AlbumInfoMapper, AlbumInfo> implements AlbumInfoService {

    @Autowired
    private AlbumAttributeValueService albumAttributeValueService;
    @Autowired
    private AlbumStatService albumStatService;

    @Autowired
    private SearchFeignClient searchFeignClient;
    //    调用kafka
    @Autowired
    private KafkaService kafkaService;

    /**
     * 保存专辑信息
     *
     * @param albumInfo
     */
    @Override
    public void saveAlbumInfo(AlbumInfo albumInfo) {
//        添加专辑id,专辑状态
        Long userId = AuthContextHolder.getUserId();
        albumInfo.setUserId(userId);
        albumInfo.setStatus(SystemConstant.ALBUM_APPROVED);
//        所有专辑还需要有免费试听的集数,比如设定试听为3集,通过比较付费类型的数值是否相同,来判断是收费还是免费的
        if (!SystemConstant.FREE_ALBUM.equals(albumInfo.getPayType())) {
//            不是免费的,就设定试听集数
            albumInfo.setTracksForFree(3);
        }
        save(albumInfo);

//        保存专辑的属性信息,上面是专辑的基本信息
        List<AlbumAttributeValue> albumPropertyValueList = albumInfo.getAlbumPropertyValueList();
        if (!CollectionUtils.isEmpty(albumPropertyValueList)) {
            for (AlbumAttributeValue albumAttributeValue : albumPropertyValueList) {
//                设置专辑id
                albumAttributeValue.setAlbumId(albumInfo.getId());
            }
//            把集合保存,不能在遍历中保存,因为一次遍历就会一次保存,严重影响性能
//            保存专辑的标签属性
            albumAttributeValueService.saveBatch(albumPropertyValueList);
        }
//        保存专辑的数据信息
        List<AlbumStat> albumStatList = buildAlbumStat(albumInfo.getId());
        albumStatService.saveBatch(albumStatList);

//       公开专辑把专辑信息添加到es中,比较封装类中的isOpen是否为公开,如果是,就添加到es中
        if (SystemConstant.OPEN_ALBUM.equals(albumInfo.getIsOpen())) {
//            添加到es中,需要远程调用search模块
            // searchFeignClient.onSaleAlbum(albumInfo.getId());
//            考虑到后期可能添加一些参数,所以为了避免调整对应的多个代码,这里使用kafka解耦，
//            这个代码发生类型转换错误,因为kafka的设置里面要求发送string类型
            kafkaService.sendMessage(KafkaConstant.ONSALE_ALBUM_QUEUE, albumInfo.getId().toString());
        }
    }

    private List<AlbumStat> buildAlbumStat(Long albumId) {
        ArrayList<AlbumStat> albumStatArrayList = new ArrayList<>();
        initAlbumStat(albumId, albumStatArrayList, SystemConstant.PLAY_NUM_ALBUM);
        initAlbumStat(albumId, albumStatArrayList, SystemConstant.SUBSCRIBE_NUM_ALBUM);
        initAlbumStat(albumId, albumStatArrayList, SystemConstant.BUY_NUM_ALBUM);
        initAlbumStat(albumId, albumStatArrayList, SystemConstant.COMMENT_NUM_ALBUM);
        return albumStatArrayList;
    }

    private void initAlbumStat(Long albumId, ArrayList<AlbumStat> albumStatArrayList, String statType) {
        AlbumStat albumStat = new AlbumStat();
        albumStat.setAlbumId(albumId);
        albumStat.setStatType(statType);
        albumStat.setStatNum(0);
    }

    /**
     * 根据id查询专辑
     *
     * @param albumId
     * @return
     */

    @TingShuCache("albumInfo")
    @Override
    public AlbumInfo getAlbumInfoById(Long albumId) {
        AlbumInfo albumInfo = getAlbumInfoFromDb(albumId);
        //AlbumInfo albumInfo = getAlbumInfoFromRedis(albumId);
        //AlbumInfo albumInfo = getAlbumFromRedisWithThreadLocal(albumId);
        //AlbumInfo albumInfo = getAlbumInfoRedisson(albumId);
        return albumInfo;
    }

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RBloomFilter albumBlooFilter;

    private AlbumInfo getAlbumInfoRedisson(Long albumId) {
//        设置锁的key用于标识
        String cacheKey = RedisConstant.ALBUM_INFO_PREFIX + albumId;
        AlbumInfo redisAlbumInfo = (AlbumInfo) redisTemplate.opsForValue().get(cacheKey);
        String lockKey = "lock-" + albumId;
//        使用redisson获取锁,如果没有,就加锁,
        RLock lock = redissonClient.getLock(lockKey);
        if (redisAlbumInfo == null) {
            try {
                lock.lock();
//                使用布隆过滤器
                boolean flag = albumBlooFilter.contains(albumId);
                if (flag) {
//                    查询数据库,并添加redis中的数据
                    AlbumInfo albumInfoDb = getAlbumInfoFromDb(albumId);
                    redisTemplate.opsForValue().set(cacheKey, albumInfoDb);
                    return albumInfoDb;
                }
            } finally {
                lock.unlock();
            }
        }
        return redisAlbumInfo;
    }


    @Autowired
    private RedisTemplate redisTemplate;
    ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public AlbumInfo getAlbumFromRedisWithThreadLocal(Long albumId) {
        String cacheKey = RedisConstant.ALBUM_INFO_PREFIX + albumId;
        AlbumInfo redisAlbumInfo = (AlbumInfo) redisTemplate.opsForValue().get(cacheKey);
        String lockKey = "lock" + albumId;
        if (redisAlbumInfo == null) {
            boolean accquireLock = false;
            String token = threadLocal.get();
            if (!StringUtils.isEmpty(token)) {
                accquireLock = true;
            } else {
                //还有很多代码要执行 1000行代码
                token = UUID.randomUUID().toString();
                accquireLock = redisTemplate.opsForValue().setIfAbsent(lockKey, token, 3, TimeUnit.SECONDS);
            }
            if (accquireLock) {
                AlbumInfo albumInfoDb = getAlbumInfoFromDb(albumId);
                redisTemplate.opsForValue().set(cacheKey, albumInfoDb);
                String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                redisScript.setScriptText(luaScript);
                redisScript.setResultType(Long.class);
                redisTemplate.execute(redisScript, Arrays.asList(lockKey), token);
                //擦屁股--风干--水洗
                threadLocal.remove();
                return albumInfoDb;
            } else {
                //目的是为了拿到锁 自旋
                while (true) {
                    SleepUtils.millis(50);
                    boolean retryAccquireLock = redisTemplate.opsForValue().setIfAbsent(lockKey, token, 3, TimeUnit.SECONDS);
                    if (retryAccquireLock) {
                        threadLocal.set(token);
                        break;
                    }
                }
                return getAlbumFromRedisWithThreadLocal(albumId);
            }
        }
        return redisAlbumInfo;
    }

    @NotNull
    private AlbumInfo getAlbumInfoFromRedis(Long albumId) {
        //        手动设置序列化,但是有个弊端是所有需要存入redis的数据都需要手动设置序列化,所以这里要做一个配置类,统一设置序列化
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
//        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
//        设置一个key
        String cachekey = RedisConstant.ALBUM_INFO_PREFIX + albumId;
//        先从缓存中查询
        AlbumInfo redisAlbumInfo = (AlbumInfo) redisTemplate.opsForValue().get(cachekey);
        if (redisAlbumInfo == null) {
//            缓存中没有,就从数据库中查询
            AlbumInfo albumInfoDb = getAlbumInfoFromDb(albumId);
//            查询出来后,放入缓存中
            redisTemplate.opsForValue().set(cachekey, albumInfoDb);
//            返回
            return albumInfoDb;
        }
        return redisAlbumInfo;
    }

    @NotNull
    private AlbumInfo getAlbumInfoFromDb(Long albumId) {
        AlbumInfo albumInfo = getById(albumId);
        LambdaQueryWrapper<AlbumAttributeValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlbumAttributeValue::getAlbumId, albumId);
        List<AlbumAttributeValue> albumAttributeValueList = albumAttributeValueService.list(wrapper);
        albumInfo.setAlbumPropertyValueList(albumAttributeValueList);
        return albumInfo;
    }

    /**
     * 更新专辑
     *
     * @param albumInfo
     */
    @Override
    public void updateAlbumInfo(AlbumInfo albumInfo) {
//        调用修改方法,把修改的数据更新
        updateById(albumInfo);
//       删除原本的标签
        LambdaQueryWrapper<AlbumAttributeValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlbumAttributeValue::getAlbumId, albumInfo.getId());
        albumAttributeValueService.remove(wrapper);
//        重新保存标签信息
        List<AlbumAttributeValue> albumPropertyValueList = albumInfo.getAlbumPropertyValueList();
        if (!CollectionUtils.isEmpty(albumPropertyValueList)) {
            for (AlbumAttributeValue albumAttributeValue : albumPropertyValueList) {
                albumAttributeValue.setAlbumId(albumInfo.getId());
            }
//            保存标签属性
            albumAttributeValueService.saveBatch(albumPropertyValueList);
        }
//       通过点击前端的设置专辑中的设为私密,设置专辑的上架和下架状态,并同步更新到es中
        //   比较封装类中的isOpen是否为公开,如果是,就更新到es中,注意这里是更新,不是添加,所以改成私密就需要下架,改成公开就需要上架
        if (SystemConstant.OPEN_ALBUM.equals(albumInfo.getIsOpen())) {
//           公开, 添加到es中,需要远程调用search模块
            searchFeignClient.onSaleAlbum(albumInfo.getId());
//            使用kafka
            kafkaService.sendMessage(KafkaConstant.ONSALE_ALBUM_QUEUE, albumInfo.getId().toString());
        } else {
//            私密,下架
            searchFeignClient.offSaleAlbum(albumInfo.getId());
//            使用kafka下架
            kafkaService.sendMessage(KafkaConstant.OFFSALE_ALBUM_QUEUE, albumInfo.getId().toString());
        }
    }

    /**
     * 删除专辑
     *
     * @param albumId
     */
    @Override
    public void deleteAlbumInfo(Long albumId) {
        removeById(albumId);
//        删除专辑的属性值
        LambdaQueryWrapper<AlbumAttributeValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlbumAttributeValue::getAlbumId, albumId);
        albumAttributeValueService.remove(wrapper);
        // TODO: 2024/2/20 还有事情做 
//        删除统计属性
        LambdaQueryWrapper<AlbumStat> statWrapper = new LambdaQueryWrapper<>();
        statWrapper.eq(AlbumStat::getAlbumId, albumId);
        albumStatService.remove(statWrapper);
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public boolean isSubscribe(Long albumId) {
        // TODO: 2024/2/25 这里有问题 userid为null
        Long userId = AuthContextHolder.getUserId();
        Query query = Query.query(Criteria.where("userId").is(userId).and("albumId").is(albumId));
        long count = mongoTemplate.count(query, MongoUtil.getCollectionName(MongoUtil.MongoCollectionEnum.USER_SUBSCRIBE, userId));

        if (count > 0) {
            return true;
        }
        return false;
    }

    /**
     * 通过专辑id集合获取专辑信息
     *
     * @param albumIdList
     * @return
     */
    @Override
    public List<AlbumTempVo> getAlbumTempVoList(List<Long> albumIdList) {
//        通过专辑id集合获取专辑信息
        List<AlbumInfo> albumInfoList = listByIds(albumIdList);
//        迭代专辑信息,把专辑信息封装到专辑临时vo中
        return albumInfoList.stream().map(albumInfo -> {
            AlbumTempVo albumTempVo = new AlbumTempVo();
            BeanUtils.copyProperties(albumInfo, albumTempVo);
            return albumTempVo;
        }).collect(Collectors.toList());
    }
}

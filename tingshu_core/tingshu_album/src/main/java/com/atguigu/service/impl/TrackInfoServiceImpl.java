package com.atguigu.service.impl;

import com.atguigu.UserInfoFeignClient;
import com.atguigu.constant.SystemConstant;
import com.atguigu.entity.AlbumInfo;
import com.atguigu.entity.TrackInfo;
import com.atguigu.entity.TrackStat;
import com.atguigu.mapper.TrackInfoMapper;
import com.atguigu.service.AlbumInfoService;
import com.atguigu.service.TrackInfoService;
import com.atguigu.service.TrackStatService;
import com.atguigu.service.VodService;
import com.atguigu.util.AuthContextHolder;
import com.atguigu.vo.AlbumTrackListVo;
import com.atguigu.vo.TrackTempVo;
import com.atguigu.vo.UserInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 声音信息 服务实现类
 * </p>
 *
 * @author 亨亨sama
 * @since 2024-01-29
 */
@Service
public class TrackInfoServiceImpl extends ServiceImpl<TrackInfoMapper, TrackInfo> implements TrackInfoService {
    @Autowired
    private AlbumInfoService albumInfoService;
    @Autowired
    private TrackStatService trackStatService;
    @Autowired
    private VodService vodService;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    /**
     * 新增声音
     *
     * @param trackInfo
     */
    @Override
    public void saveTrackInfo(TrackInfo trackInfo) {
//        保存声音的基本信息,看上传的数据中哪些在trackInfo中没有,没有就需要单独添加
        trackInfo.setUserId(AuthContextHolder.getUserId());
        trackInfo.setStatus(SystemConstant.TRACK_APPROVED);
//        查询专辑中声音编号最大的值,这里是上传每个声音前面的编号
        LambdaQueryWrapper<TrackInfo> wrapper = new LambdaQueryWrapper<>();
//        比较上传的声音的专辑id是否和本来的专辑id相同
        wrapper.eq(TrackInfo::getAlbumId, trackInfo.getAlbumId());
//        拿到id之后进行排序,升序降序都可以,这里使用升序取最后一个,即最大值
        wrapper.orderByAsc(TrackInfo::getOrderNum);
//        通过调用getOrderNum得到结果进行查询
        wrapper.select(TrackInfo::getOrderNum);
//        把最后一个sql的结果取出来，即取到最大值
        wrapper.last("limit 1");
//        getone只能取一条数据，它会返回一个实例对象。
//        所以wrapper是多条数据会报错，一般和.last(“limit 1”)一起使用
        TrackInfo maxOrderNoTrackInfo = getOne(wrapper);
        int orderNum = 1;
        if (maxOrderNoTrackInfo != null) {
            orderNum = maxOrderNoTrackInfo.getOrderNum() + 1;
        }
        trackInfo.setOrderNum(orderNum);
//        保存声音的基本信息
        save(trackInfo);

//        更新专辑的声音个数
//        通过声音表获取到专辑id，通过id查询到专辑
        AlbumInfo albumInfo = albumInfoService.getById(trackInfo.getAlbumId());
        int includeTrackCount = albumInfo.getIncludeTrackCount() + 1;
        albumInfo.setIncludeTrackCount(includeTrackCount);
        albumInfoService.updateById(albumInfo);

//        初始化声音的统计信息
//        这里的逻辑顺序是,先通过方法调用id和类型,在下面生成的私有方法中,添加属性,后面在把属性添加到集合中
//        再创建新的方法,把最开始调用的方法放入,且根据需要的类型,分别调用封装的方法,最后返回一个集合,再再最外层,把这个的所有数据保存到表中
        List<TrackStat> trackStatList = buildTrackStat(trackInfo.getId());
        trackStatService.saveBatch(trackStatList);
    }


    private List<TrackStat> buildTrackStat(Long trackId) {
        ArrayList<TrackStat> trackStatArrayList = new ArrayList<>();
        initTrackStat(trackId, trackStatArrayList, SystemConstant.PLAY_NUM_TRACK);
        initTrackStat(trackId, trackStatArrayList, SystemConstant.SUBSCRIBE_NUM_ALBUM);
        initTrackStat(trackId, trackStatArrayList, SystemConstant.BUY_NUM_ALBUM);
        initTrackStat(trackId, trackStatArrayList, SystemConstant.COMMENT_NUM_TRACK);
        return trackStatArrayList;
    }

    //
    private void initTrackStat(Long trackId, ArrayList<TrackStat> trackStatArrayList, String statType) {
        TrackStat trackStat = new TrackStat();
        trackStat.setTrackId(trackId);
        trackStat.setStatType(statType);
        trackStat.setStatNum(0);
//        在单独创建一个数组,把trackstat放进去
        trackStatArrayList.add(trackStat);
    }

    /**
     * 更新声音
     *
     * @param trackInfo
     */
    @Override
    public void updateTrackInfoById(TrackInfo trackInfo) {
//        直接调用修改声音的方法
        vodService.getTrackMediaInfo(trackInfo);
//        再通过updateById方法,把修改后的trackInfo上传表
        updateById(trackInfo);
    }

    /**
     * 通过id删除声音
     *
     * @param trackId
     */
    @Override
    public void deleteTrackInfo(Long trackId) {
//        更新专辑的声音个数
        TrackInfo trackInfo = getById(trackId);
        AlbumInfo albumInfo = albumInfoService.getById(trackInfo.getAlbumId());
        albumInfo.setIncludeTrackCount(albumInfo.getIncludeTrackCount() - 1);
        albumInfoService.updateAlbumInfo(albumInfo);
//        调用腾讯云声音
        vodService.removeTrack(trackInfo.getMediaFileId());
//        删除统计信息
        trackStatService.remove(new LambdaQueryWrapper<TrackStat>().eq(TrackStat::getTrackId, trackId));
    }

    /**
     * 通过专辑id获取声音分页列表
     *
     * @param pageParam
     * @param trackId
     * @return
     */

    @Autowired
    private TrackInfoMapper trackInfoMapper;

    @Override
    public IPage<AlbumTrackListVo> getAlbumDetailTrackByPage(IPage<AlbumTrackListVo> pageParam, Long albumId) {
        //        获取到用户id
        Long userId = AuthContextHolder.getUserId();
//        如果要测试未登录状态,把这个id设置为null就可以了
//        Long userId = null;
//        通过专辑id,找到专辑信息

        AlbumInfo albumInfo = albumInfoService.getById(albumId);
//        获取专辑声音和专辑的统计信息,直接去数据库查询
        pageParam = baseMapper.getAlbumDetailTrackByPage(pageParam, albumId);
//       获取所有声音
        List<AlbumTrackListVo> trackListVoList = pageParam.getRecords();
//      判断用户是否登录
        if (userId == null) {
//            没有登录时,分为两种情况,一种是免费,一种是付费
//            如果是收费的,就把试听之后的图标改成付费的标志
            if (!SystemConstant.FREE_ALBUM.equals(albumInfo.getPayType())) {
//                获取需要付费的声音列表,当声音的orderNum大于免费的试听集数,那就是要付费的
                trackListVoList.forEach(item -> {
//                    遍历所有声音,当它的排序数字大于试听数字时(这里的排序数字是根据sql中设置的排序顺序来的),就把图标改成付费的标志
                    if (item.getOrderNum() > albumInfo.getTracksForFree()) {
                        item.setIsShowPaidMark(true);
                    }
                });
            }
        }

        //            定义一个值，表示用户是否需要付费
        boolean needPay = false;
//                当用户登录 ,先查看是否为vip免费
        if (SystemConstant.VIPFREE_ALBUM.equals(albumInfo.getPayType())) {
//                    判断是否为vip,通过用户id查询用户是否为vip，通过远程ip,就把图标改成vip的标志
            UserInfoVo userInfoVo = userInfoFeignClient.getUserInfo(userId).getData();
            Integer isVip = userInfoVo.getIsVip();

//                    如果不是vip,就把图标试听免费，其他付费的标志
            if (isVip != 1) {
                needPay = true;
            } else if (isVip == 1 && userInfoVo.getVipExpireTime().getTime() < System.currentTimeMillis()) {
                //                    如果是vip，但是vip已经过期了，就把图标改成付费的标志
                needPay = true;
            } else {
//                        如果是vip，且vip没有过期，就把图标改成vip免费的标志
                needPay = false;
            }
        } else {
//            是vip付费的情况
            if (SystemConstant.NEED_PAY_ALBUM.equals(albumInfo.getPayType())) {
//                获取需要付费的声音列表,当声音的orderNum大于免费的试听集数,那就是要付费的
                needPay = true;
            }
        }
//        当用户登录，且需要付费，就把图标改成付费的标志
//        但是需要判断有用户是否已经购买了专辑或者里面的声音
        if (needPay) {
//            获取需要付费的声音列表，使用stream流的方式，过滤出需要付费的声音
            List<AlbumTrackListVo> trackNeedPayList = trackListVoList.stream().filter(item ->
                            item.getOrderNum() > albumInfo.getTracksForFree())
                    .collect(Collectors.toList());
//           判断是否为空，并拿到需要付费的id
            if (!CollectionUtils.isEmpty(trackNeedPayList)){
                List<Long> trackNeedPayIdList = trackNeedPayList.stream().map(AlbumTrackListVo::getTrackId).collect(Collectors.toList());
//               查询用户是否已经购买了这个声音(从userpaidalbum和userpaidtrack查询),远程调用接口
                Map<Long, Boolean> paidMarkMap = userInfoFeignClient.getUserShowPaidMarkOrNot(albumId, trackNeedPayIdList).getData();
                trackNeedPayList.forEach((item)->{
                    item.setIsShowPaidMark(paidMarkMap.get(item.getTrackId()));
                });
            }
        }
        //       如果是免费的,就直接返回,因为图标默认是false,不需要改
        return pageParam;
    }

    /**
     * 根据声音id列表获取声音信息列表
     *
     * @param trackIdList
     * @return
     */
    @Override
    public List<TrackTempVo> getTrackVoList(List<Long> trackIdList) {
        List<TrackInfo> trackInfoList = listByIds(trackIdList);
//        这个数值需要返回给前端，所以需要把这个转化为vo对象，传回
        List<TrackTempVo> trackTempVoList = trackInfoList.stream().map(trackInfo -> {
            TrackTempVo trackTempVo = new TrackTempVo();
            BeanUtils.copyProperties(trackInfo, trackTempVo);
            trackTempVo.setTrackId(trackInfo.getId());
            return trackTempVo;
        }).collect(Collectors.toList());
        return trackTempVoList;
    }
}



package com.atguigu.service.impl;

import com.atguigu.AlbumFeignClient;
import com.atguigu.UserInfoFeignClient;
import com.atguigu.constant.SystemConstant;
import com.atguigu.entity.*;
import com.atguigu.mapper.UserInfoMapper;
import com.atguigu.service.*;
import com.atguigu.util.AuthContextHolder;
import com.atguigu.vo.UserPaidRecordVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author 亨亨sama
 * @since 2024-01-29
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private UserPaidAlbumService userPaidAlbumService;
    @Autowired
    private UserPaidTrackService userPaidTrackService;
    @Autowired
    private AlbumFeignClient albumFeignClient;
    @Autowired
    private UserVipInfoService userVipInfoService;
    @Autowired
    private VipServiceConfigService vipServiceConfig;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    @Override
    public Map<Long, Boolean> getUserShowPaidMarkOrNot(Long albumId, List<Long> needPayTrackIdList) {
        HashMap<Long, Boolean> showPaidMarkMap = new HashMap<>();
        //  查询用户购买过的专辑
//        通过用户id和专辑id去查询
        Long userId = AuthContextHolder.getUserId();
//        因为是从数据库中查询,所以直接使用mybatis-plus
        LambdaQueryWrapper<UserPaidAlbum> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPaidAlbum::getUserId, userId);
        wrapper.eq(UserPaidAlbum::getAlbumId, albumId);
        UserPaidAlbum userPaidAlbum = userPaidAlbumService.getOne(wrapper);
//        判断得到的数据是否为空,不为空则说明已经购买过专辑
        if (userPaidAlbum != null) {
//            使用map进行存放,id作为key ,图标作为值
            needPayTrackIdList.forEach(trackId -> {
                showPaidMarkMap.put(trackId, false);
            });
            return showPaidMarkMap;
        } else {
            //  查询用户购买过的声音
            LambdaQueryWrapper<UserPaidTrack> trackWrapper = new LambdaQueryWrapper<>();
            trackWrapper.eq(UserPaidTrack::getUserId, userId);
//            in方法将创建一个条件，检查每一行的track_id是否在此列表中。
            trackWrapper.in(UserPaidTrack::getTrackId, needPayTrackIdList);
//            获取到已经购买过的声音的集合
            List<UserPaidTrack> userPaidTrackList = userPaidTrackService.list(trackWrapper);
//            将已经购买过的声音的id集合放入list中
            List<Long> paidTrackIdList = userPaidTrackList.stream().map(UserPaidTrack::getTrackId).collect(Collectors.toList());
            needPayTrackIdList.forEach(trackId -> {
//               判断trackid是否在需要购买的id集合范围内
                if (paidTrackIdList.contains(trackId)) {
//                    在范围内,则不需要购买
                    showPaidMarkMap.put(trackId, false);
                } else {
                    showPaidMarkMap.put(trackId, true);
                }
            });
        }
        return showPaidMarkMap;
    }

    /** 更新用户支付信息
     *
     * @param userPaidRecordVo
     */
    @Override
    public void updateUserPaidRecord(UserPaidRecordVo userPaidRecordVo) {
        //如果购买的是专辑
//        判断什么时候购买的是专辑
        if (SystemConstant.BUY_ALBUM.equals(userPaidRecordVo.getItemType())) {
//            保存在表中userpaidalbum,count是查询表中符合条件的数量
//            这里是通过订购单号查询,看看是否有重复提交订单
            long count = userPaidAlbumService.count(new LambdaQueryWrapper<UserPaidAlbum>()
                    .eq(UserPaidAlbum::getOrderNo, userPaidRecordVo.getOrderNo()));
//            大于0,说明订单有重复提交,直接返回
            if (count > 0) return;
//                没有,就把数据提交
                UserPaidAlbum userPaidAlbum = new UserPaidAlbum();
                userPaidAlbum.setUserId(userPaidRecordVo.getUserId());
                userPaidAlbum.setAlbumId(userPaidRecordVo.getItemIdList().get(0));
                userPaidAlbum.setOrderNo(userPaidRecordVo.getOrderNo());
                userPaidAlbumService.save(userPaidAlbum);
        }
//       购买的是声音
        else if (SystemConstant.BUY_TRACK.equals(userPaidRecordVo.getItemType())) {
            long count = userPaidTrackService.count(new LambdaQueryWrapper<UserPaidTrack>()
                    .eq(UserPaidTrack::getOrderNo, userPaidRecordVo.getOrderNo()));
            if (count > 0) return;
//            需要添加的数据可以通过表和封装类得知
            UserPaidTrack userPaidTrack = new UserPaidTrack();

//            得到声音id和声音列表,这里的getItemIdList,表示得到其类型的id的集合,因为这个if是判断购买的声音,所以它就表示声音
            TrackInfo trackInfo = albumFeignClient.getTrackInfoById(userPaidRecordVo.getItemIdList().get(0)).getData();
            List<UserPaidTrack> trackIdList = userPaidRecordVo.getItemIdList().stream().map(trackId -> {
                userPaidTrack.setUserId(userPaidRecordVo.getUserId());
                userPaidTrack.setAlbumId(trackInfo.getAlbumId());
                userPaidTrack.setTrackId(trackId);
                userPaidTrack.setOrderNo(userPaidRecordVo.getOrderNo());
                return userPaidTrack;
            }).collect(Collectors.toList());
//            把得到的声音id集合统一传上去
            userPaidTrackService.saveBatch(trackIdList);
        }
//        购买的是vip
        else if (SystemConstant.BUY_VIP.equals(userPaidRecordVo.getItemType())) {
            long count = userVipInfoService.count(new LambdaQueryWrapper<UserVipInfo>()
                    .eq(UserVipInfo::getOrderNo, userPaidRecordVo.getOrderNo()));
            if (count > 0) return;
            UserVipInfo userVipInfo = new UserVipInfo();
            userVipInfo.setUserId(userPaidRecordVo.getUserId());
            userVipInfo.setOrderNo(userPaidRecordVo.getOrderNo());
//            得到用户vip的配置id
            Long vipConfigId = userPaidRecordVo.getItemIdList().get(0);
            VipServiceConfig vipConfig = vipServiceConfig.getById(vipConfigId);
            Date startTime = new Date();
            //拿到用户信息
            UserInfo userInfo = getById(userPaidRecordVo.getUserId());
            //判断当前用户是否为vip 如果是vip并且没有过期 vip时间要累加
            if (userInfo.getIsVip() == 1 && userInfo.getVipExpireTime().after(new Date()) ) {
                startTime = userInfo.getVipExpireTime();
            }
//
            Date newExpireTime = new DateTime(startTime).plusMonths(vipConfig.getServiceMonth()).toDate();
//            设置vip用户的开始和结束时间,并上传
            userVipInfo.setStartTime(startTime);
            userVipInfo.setExpireTime(newExpireTime);
            userVipInfoService.save(userVipInfo);

//            更新用户表中的vip信息
            userInfo.setIsVip(1);
            userInfo.setVipExpireTime(newExpireTime);
            updateById(userInfo);
        }
    }
}

package com.atguigu.service.impl;

import com.atguigu.entity.UserInfo;
import com.atguigu.entity.UserPaidAlbum;
import com.atguigu.entity.UserPaidTrack;
import com.atguigu.mapper.UserInfoMapper;
import com.atguigu.service.UserInfoService;
import com.atguigu.service.UserPaidAlbumService;
import com.atguigu.service.UserPaidTrackService;
import com.atguigu.util.AuthContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}

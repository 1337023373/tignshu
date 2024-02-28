package com.atguigu.controller;

import com.atguigu.entity.UserInfo;
import com.atguigu.entity.UserPaidTrack;
import com.atguigu.login.TingShuLogin;
import com.atguigu.result.RetVal;
import com.atguigu.service.UserInfoService;
import com.atguigu.service.UserPaidTrackService;
import com.atguigu.util.AuthContextHolder;
import com.atguigu.vo.UserInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户 前端控制器
 * </p>
 *
 * @author 亨亨sama
 * @since 2024-01-29
 */
@RestController
@RequestMapping("/api/user/userInfo")

public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private UserPaidTrackService userPaidTrackService;

    @Operation(summary = "获取用户个人信息")
    @GetMapping("/getUserInfo/{userId}")
    public RetVal<UserInfoVo> getUserInfo(@PathVariable Long userId) {
        UserInfo userInfo = userInfoService.getById(userId);
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo, userInfoVo);
        return RetVal.ok(userInfoVo);
    }

    @TingShuLogin
    @Operation(summary = "获取用户是否需要购买的标识")
    @PostMapping("/getUserShowPaidMarkOrNot/{albumId}")
//    通过专辑id和需要付费的声音id,去表中查找
    public RetVal<Map<Long, Boolean>> getUserShowPaidMarkOrNot(@PathVariable Long albumId, @RequestBody List<Long> trackNeedPayIdList) {
        Map<Long, Boolean> retMap = userInfoService.getUserShowPaidMarkOrNot(albumId, trackNeedPayIdList);
        return RetVal.ok(retMap);
    }

    @Operation(summary = "获取用户已经购买的声音")
    @GetMapping("/getUserInfo/{albumId}")
    public RetVal<List<Long>> getPaidTrackIdList(@PathVariable Long albumId) {
//        直接通过专辑id去数据库查找,使用mybatis-plus的方法
        Long userId = AuthContextHolder.getUserId();
        LambdaQueryWrapper<UserPaidTrack> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPaidTrack::getAlbumId, albumId);
        wrapper.eq(UserPaidTrack::getUserId, userId);
        List<UserPaidTrack> userPaidTrackList = userPaidTrackService.list(wrapper);
//        迭代器遍历,将id放入list中
        List<Long> paidTrackIdList = userPaidTrackList.stream().map(UserPaidTrack::getTrackId)
                .collect(Collectors.toList());
        return RetVal.ok(paidTrackIdList);
    }
}

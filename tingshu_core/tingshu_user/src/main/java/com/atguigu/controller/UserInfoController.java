package com.atguigu.controller;

import com.atguigu.entity.UserInfo;
import com.atguigu.login.TingShuLogin;
import com.atguigu.result.RetVal;
import com.atguigu.service.UserInfoService;
import com.atguigu.vo.UserInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
}

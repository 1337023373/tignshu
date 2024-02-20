package com.atguigu.controller;

import com.atguigu.entity.UserInfo;
import com.atguigu.result.RetVal;
import com.atguigu.service.UserInfoService;
import com.atguigu.vo.UserInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

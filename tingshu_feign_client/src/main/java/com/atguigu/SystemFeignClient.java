package com.atguigu;

import com.atguigu.entity.SysLoginLog;
import com.atguigu.entity.SysUser;
import com.atguigu.result.RetVal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "tingshu-system")
public interface SystemFeignClient {
    /**
     * 记录登录日志
     * @param sysLoginLog
     * @return
     */
    @PostMapping("/admin/system/sysLoginLog/recordLoginLog")
    RetVal recordLoginLog(@RequestBody SysLoginLog sysLoginLog);
    /**
     * 根据用户名获取用户信息
     * @param username
     * @return
     */
    @GetMapping("/admin/system/securityLogin/getByUsername/{username}")
    RetVal<SysUser> getByUsername(@PathVariable("username") String username);

    /**
     * 获取用户按钮权限
     * @param userId
     * @return
     */
    @GetMapping("/admin/system/securityLogin/findUserPermsList/{userId}")
    RetVal<List<String>> findUserPermsList(@PathVariable("userId") Long userId);
}
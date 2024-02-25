package com.atguigu.controller;

import com.atguigu.entity.VipServiceConfig;
import com.atguigu.result.RetVal;
import com.atguigu.service.VipServiceConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "会员配置接口")
@RestController
@RequestMapping("/api/user/vipConfig")

public class VipController {
    @Autowired
    private VipServiceConfigService vipConfigService;
    @Operation(summary = "获取所有vip配置")
    @GetMapping("/findAllVipConfig")
    public RetVal findAllVipConfig() {
        List<VipServiceConfig> list = vipConfigService.list();
        return RetVal.ok(list);
    }
}

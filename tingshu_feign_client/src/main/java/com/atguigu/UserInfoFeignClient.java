package com.atguigu;

import com.atguigu.entity.VipServiceConfig;
import com.atguigu.result.RetVal;
import com.atguigu.vo.UserInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(value = "tingshu-user")
public interface UserInfoFeignClient {
    @GetMapping("/api/user/userInfo/getUserInfo/{userId}")
     RetVal<UserInfoVo> getUserInfo(@PathVariable Long userId) ;

    @PostMapping("/api/user/userInfo/getUserShowPaidMarkOrNot/{albumId}")
//    通过专辑id和需要付费的声音id,去表中查找
    RetVal<Map<Long, Boolean>> getUserShowPaidMarkOrNot(@PathVariable Long albumId, @RequestBody List<Long> trackNeedPayIdList);

    //    通过专辑id,找到已经购买的声音id列表
    @GetMapping("/api/user/userInfo/getPaidTrackIdList/{albumId}")
    public RetVal<List<Long>> getPaidTrackIdList(@PathVariable Long albumId);

    //根据vip获取单个vip配置
    @GetMapping("/api/user/vipConfig/getVipConfig/{id}")
    public VipServiceConfig getVipConfig(@PathVariable Long id);
}

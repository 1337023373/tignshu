package com.atguigu;

import com.atguigu.result.RetVal;
import com.atguigu.vo.UserInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "tingshu-user")
public interface UserInfoFeignClient {
    @GetMapping("/api/user/userInfo/getUserInfo/{userId}")
     RetVal<UserInfoVo> getUserInfo(@PathVariable Long userId) ;
}

package com.atguigu.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.atguigu.constant.RedisConstant;
import com.atguigu.entity.UserInfo;
import com.atguigu.login.TingShuLogin;
import com.atguigu.result.RetVal;
import com.atguigu.service.UserInfoService;
import com.atguigu.util.AuthContextHolder;
import com.atguigu.vo.UserInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Tag(name = "用户登录接口")
@RestController
@RequestMapping("/api/user/wxLogin")
//url: `/api/user/wxLogin/wxLogin/${code}`,
public class WeiXinLoginController {


//    这里mybatisPlus是生成的关于userinfo表中的sql语句
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private WxMaService wxMaService;

    @Autowired
    private RedisTemplate redisTemplate;
//   官网流程，前端通过调用wx.login（）获取code，并传给服务器
    @Operation(summary = "通过code获取/保存用户信息")
    @GetMapping("/wxLogin/{code}")
    public RetVal  wxLogin(@PathVariable String code) throws WxErrorException {
//  调用 auth.code2Session 接口，换取 用户唯一标识 OpenID 、
//  用户在微信开放平台账号下的唯一标识UnionID（若当前小程序已绑定到微信开放平台账号） 和 会话密钥 session_key。
        WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
        String openid = sessionInfo.getOpenid();
//        拿到id就去数据库查询用户信息是否存在，
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("wx_open_id", openid);
//        通过plus中的getone方法，去查询openid是否存在
        UserInfo userInfo = userInfoService.getOne(queryWrapper);

//        如果数据库中不存在，就添加到表中
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setNickname("听友" + System.currentTimeMillis());
            userInfo.setAvatarUrl("https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
            userInfo.setWxOpenId(openid);
            //是否为会员  不是会员
            userInfo.setIsVip(0);
            userInfoService.save(userInfo);
        }
        //如果数据库存在 往redis里面存储用户信息,RedisConstant是一个封装类
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String userkey = RedisConstant.USER_LOGIN_KEY_PREFIX + uuid;
        redisTemplate.opsForValue().set(userkey,userInfo,RedisConstant.USER_LOGIN_KEY_TIMEOUT, TimeUnit.HOURS);

//        把token封装后返回
        Map<String, Object> remap = new HashMap<>();
        remap.put("token", uuid);
        return RetVal.ok(remap);
    }

    @TingShuLogin
    @Operation(summary = "获取用户个人信息")
    @GetMapping("/getUserInfo")
    public RetVal getUserInfo() {
        //获取当前线程的用户id
        Long userId = AuthContextHolder.getUserId();
        UserInfo userInfo = userInfoService.getById(userId);
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo, userInfoVo);
        return RetVal.ok(userInfoVo);
    }
}

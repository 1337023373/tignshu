package com.atguigu.service.impl;

import com.atguigu.entity.UserInfo;
import com.atguigu.mapper.UserInfoMapper;
import com.atguigu.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}

package com.atguigu.service;

import com.atguigu.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户 服务类
 * </p>
 *
 * @author 亨亨sama
 * @since 2024-01-29
 */
public interface UserInfoService extends IService<UserInfo> {

    Map<Long, Boolean> getUserShowPaidMarkOrNot(Long albumId, List<Long> needPayTrackIdList);
}

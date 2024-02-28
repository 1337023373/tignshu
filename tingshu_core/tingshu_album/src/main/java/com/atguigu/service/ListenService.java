package com.atguigu.service;

import com.atguigu.vo.UserListenProcessVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;
import java.util.HashMap;

public interface ListenService {
    void updatePlaySecond(UserListenProcessVo userListenProcessVo);

    BigDecimal getLastPlaySecond(Long trackId);

    HashMap<String, Object> getRecentlyPlay();

    boolean collectTrack(Long trackId);

    boolean isCollected(Long trackId);

    Page getUserCollectByPage(Integer page, Integer pageSize);
}

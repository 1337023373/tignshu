package com.atguigu.controller;

import com.atguigu.login.TingShuLogin;
import com.atguigu.mapper.TrackInfoMapper;
import com.atguigu.result.RetVal;
import com.atguigu.service.ListenService;
import com.atguigu.vo.TrackStatVo;
import com.atguigu.vo.UserListenProcessVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "听专辑管理")
@RestController
@RequestMapping("/api/album/progress/")
public class ListenController {
    @Autowired
    private ListenService listenService;
    @Autowired
    private TrackInfoMapper trackInfoMapper;

    //http://127.0.0.1/api/album/progress/updatePlaySecond
    @Operation(summary = "更新播放进度")
    @PostMapping("updatePlaySecond")
    @TingShuLogin
    public RetVal updatePlaySecond(@RequestBody UserListenProcessVo userListenProcessVo) {
        listenService.updatePlaySecond(userListenProcessVo);
        return RetVal.ok();
    }

    //    http://127.0.0.1/api/album/progress/getLastPlaySecond/49161
    @Operation(summary = "自动追踪上一次进度")
    @GetMapping("getLastPlaySecond/{trackId}")
    @TingShuLogin
    public RetVal getLastPlaySecond(@PathVariable Long trackId) {
        BigDecimal lastPlaySecond = listenService.getLastPlaySecond(trackId);
        return RetVal.ok(lastPlaySecond);
    }

    //http://127.0.0.1/api/album/progress/getRecentlyPlay
    @Operation(summary = "获取最近播放记录")
    @GetMapping("getRecentlyPlay")
    @TingShuLogin
    public RetVal<Map<String, Object>> getRecentlyPlay() {
        HashMap<String, Object> recentlyPlay = listenService.getRecentlyPlay();
        return RetVal.ok(recentlyPlay);
    }


    @Operation(summary = "4.获取声音统计信息")
    @GetMapping("getTrackStatistics/{trackId}")
    public RetVal<TrackStatVo> getTrackStatistics(@PathVariable Long trackId) {
        TrackStatVo trackStatVo = trackInfoMapper.getTrackStatistics(trackId);
        return RetVal.ok(trackStatVo);
    }

    @TingShuLogin
    @Operation(summary = "点击收藏声音")
    @GetMapping("collectTrack/{trackId}")
    public RetVal<Boolean> collectTrack(@PathVariable Long trackId) {
        boolean flag = listenService.collectTrack(trackId);
        return RetVal.ok(flag);
    }

    @TingShuLogin
    @Operation(summary = "是否收藏声音")
    @GetMapping("isCollect/{trackId}")
    public RetVal<Boolean> isCollected(@PathVariable Long trackId) {
        boolean flag = listenService.isCollected(trackId);
        return RetVal.ok(flag);
    }

    //    http://127.0.0.1/api/album/progress/getUserCollectByPage/1/10
    @Operation(summary = "分页获取用户收藏声音列表")
    @GetMapping("getUserCollectByPage/{page}/{pageSize}")
    @TingShuLogin
    public RetVal getUserCollectByPage(@PathVariable Integer page, @PathVariable Integer pageSize) {
        IPage userCollectByPage = listenService.getUserCollectByPage(page, pageSize);
        return RetVal.ok(userCollectByPage);
    }

    //http://127.0.0.1/api/album/progress/getPlayHistoryTrackByPage/1/10
    @Operation(summary = "分页获取播放历史")
    @GetMapping("getPlayHistoryTrackByPage/{page}/{pageSize}")
    @TingShuLogin
    public RetVal getPlayHistoryTrackByPage(@PathVariable Integer page, @PathVariable Integer pageSize) {
        IPage pageParam = listenService.getPlayHistoryTrackByPage(page, pageSize);
        return RetVal.ok(pageParam);
    }
}
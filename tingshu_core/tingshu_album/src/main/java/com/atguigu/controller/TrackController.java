package com.atguigu.controller;

import com.atguigu.entity.TrackInfo;
import com.atguigu.login.TingShuLogin;
import com.atguigu.mapper.TrackInfoMapper;
import com.atguigu.query.TrackInfoQuery;
import com.atguigu.result.RetVal;
import com.atguigu.service.TrackInfoService;
import com.atguigu.util.AuthContextHolder;
import com.atguigu.vo.TrackTempVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "声音接口")
@RestController
@RequestMapping("/api/album/trackInfo")
public class TrackController {

    @Autowired
    private TrackInfoMapper trackInfoMapper;

    @Autowired
    private TrackInfoService trackInfoService;

    @TingShuLogin
    @Operation(summary = "获取当前用户声音分页列表")
    @PostMapping("findUserTrackPage/{pageNum}/{pageSize}")
    public RetVal findUserTrackPage(@PathVariable long pageNum,
                                    @PathVariable long pageSize,
                                    @RequestBody TrackInfoQuery trackInfoQuery) {
//        通过用户id拿到对应的数据
        Long userId = AuthContextHolder.getUserId();
        trackInfoQuery.setUserId(userId);
//        拿到数据后,分页展示
        IPage<TrackTempVo> pageParam = new Page<>(pageNum, pageSize);
        pageParam = trackInfoMapper.findUserTrackPage(pageParam, trackInfoQuery);
        return RetVal.ok(pageParam);
    }

    @TingShuLogin
    @Operation(summary = "新增声音")
    @PostMapping("saveTrackInfo")
    public RetVal saveTrackInfo(@RequestBody TrackInfo trackInfo) {
        trackInfoService.saveTrackInfo(trackInfo);
        return RetVal.ok();
    }

    @TingShuLogin
    @Operation(summary = "根据id获取声音信息")
    @GetMapping("getTrackInfoById/{trackId}")
    public RetVal getTrackInfoById(@PathVariable Long trackId) {
        TrackInfo trackInfo = trackInfoService.getById(trackId);
        return RetVal.ok(trackInfo);
    }

    @TingShuLogin
    @Operation(summary = "修改声音")
    @PutMapping("updateTrackInfoById")
    public RetVal updateTrackInfoById(@RequestBody TrackInfo trackInfo) {
        trackInfoService.updateTrackInfoById(trackInfo);
        return RetVal.ok();
    }

    @TingShuLogin
    @Operation(summary = "删除声音")
    @DeleteMapping("deleteTrackInfo/{trackId}")
    public RetVal deleteTrackInfo(@PathVariable Long trackId) {
        trackInfoService.deleteTrackInfo(trackId);
        return RetVal.ok();
    }
}

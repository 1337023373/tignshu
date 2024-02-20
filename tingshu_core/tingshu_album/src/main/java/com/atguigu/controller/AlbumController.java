package com.atguigu.controller;

import com.atguigu.entity.AlbumInfo;
import com.atguigu.login.TingShuLogin;
import com.atguigu.mapper.AlbumInfoMapper;
import com.atguigu.query.AlbumInfoQuery;
import com.atguigu.result.RetVal;
import com.atguigu.service.AlbumInfoService;
import com.atguigu.util.AuthContextHolder;
import com.atguigu.vo.AlbumTempVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "专辑管理")
@RestController
@RequestMapping("/api/album/albumInfo")
public class AlbumController {

    @Autowired
    private AlbumInfoService albumInfoService;
    @Autowired
    private AlbumInfoMapper albumInfoMapper;

    @Operation(summary = "分页查询")
    @TingShuLogin
    @PostMapping("/getUserAlbumByPage/{page}/{limit}")
    public RetVal getUserAlbumByPage(@PathVariable Long page, @PathVariable Long limit, @RequestBody AlbumInfoQuery albumInfoQuery) {
//        要查询对应的专辑,肯定需要id去查,那么一定需要登录
        Long userId = AuthContextHolder.getUserId();
        albumInfoQuery.setUserId(userId);
        IPage<AlbumTempVo> pageParam = new Page<>(page, limit);
        pageParam = albumInfoMapper.getUserAlbumByPage(pageParam, albumInfoQuery);
        return RetVal.ok(pageParam);
    }

    @Operation(summary = "保存专辑信息")
    @TingShuLogin
    @PostMapping("/saveAlbumInfo")
    public RetVal saveAlbumInfo(@RequestBody  AlbumInfo albumInfo) {
        albumInfoService.saveAlbumInfo(albumInfo);
        return RetVal.ok();
    }

    @Operation(summary = "根据id查询专辑")
    @GetMapping("/getAlbumInfoById/{albumId}")
    public RetVal<AlbumInfo> getAlbumInfoById(@PathVariable Long albumId) {
        AlbumInfo albumInfo = albumInfoService.getAlbumInfoById(albumId);
        return RetVal.ok(albumInfo);
    }

    @Operation(summary = "保存专辑信息")
    @PutMapping("/updateAlbumInfo")
    public RetVal updateAlbumInfo(@RequestBody AlbumInfo albumInfo) {
        albumInfoService.updateAlbumInfo(albumInfo);
        return RetVal.ok();
    }

    @TingShuLogin
    @Operation(summary = "删除专辑信息")
    @DeleteMapping("deleteAlbumInfo/{albumId}")
    public RetVal deleteAlbumInfo(@PathVariable Long albumId) {
        albumInfoService.deleteAlbumInfo(albumId);
        return RetVal.ok();
    }
}

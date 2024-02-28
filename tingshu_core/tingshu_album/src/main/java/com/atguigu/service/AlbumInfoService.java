package com.atguigu.service;

import com.atguigu.entity.AlbumInfo;
import com.atguigu.vo.AlbumTempVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 专辑信息 服务类
 * </p>
 *
 * @author 亨亨sama
 * @since 2024-01-29
 */
public interface AlbumInfoService extends IService<AlbumInfo> {

    void saveAlbumInfo(AlbumInfo albumInfo);

    AlbumInfo getAlbumInfoById(Long albumId);

    void updateAlbumInfo(AlbumInfo albumInfo);

    void deleteAlbumInfo(Long albumId);


    boolean isSubscribe(Long albumId);

    List<AlbumTempVo> getAlbumTempVoList(List<Long> albumIdList);
}

package com.atguigu.service;

import com.atguigu.entity.TrackInfo;
import com.atguigu.vo.AlbumTrackListVo;
import com.atguigu.vo.TrackTempVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 声音信息 服务类
 * </p>
 *
 * @author 亨亨sama
 * @since 2024-01-29
 */
public interface TrackInfoService extends IService<TrackInfo> {

    void saveTrackInfo(TrackInfo trackInfo);

    void updateTrackInfoById(TrackInfo trackInfo);

    void deleteTrackInfo(Long trackId);

    IPage<AlbumTrackListVo> getAlbumDetailTrackByPage(IPage<AlbumTrackListVo> pageParam, Long albumId);

    List<TrackTempVo> getTrackVoList(List<Long> trackIdList);
}

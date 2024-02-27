package com.atguigu.mapper;

import com.atguigu.entity.TrackInfo;
import com.atguigu.query.TrackInfoQuery;
import com.atguigu.vo.AlbumTrackListVo;
import com.atguigu.vo.TrackStatVo;
import com.atguigu.vo.TrackTempVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 声音信息 Mapper 接口
 * </p>
 *
 * @author 亨亨sama
 * @since 2024-01-29
 */
public interface TrackInfoMapper extends BaseMapper<TrackInfo> {

    IPage<TrackTempVo> findUserTrackPage(IPage<TrackTempVo> pageParam, TrackInfoQuery trackInfoQuery);

    IPage<AlbumTrackListVo> getAlbumDetailTrackByPage(@Param("pageParam") IPage<AlbumTrackListVo> pageParam, @Param("albumId") Long albumId);

    TrackStatVo getTrackStatistics(Long trackId);
}

package com.atguigu.mapper;

import com.atguigu.entity.AlbumStat;
import com.atguigu.vo.AlbumStatVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 专辑统计 Mapper 接口
 * </p>
 *
 * @author 亨亨sama
 * @since 2024-01-29
 */
public interface AlbumStatMapper extends BaseMapper<AlbumStat> {

    AlbumStatVo getAlbumStatInfo(Long albumId);
}

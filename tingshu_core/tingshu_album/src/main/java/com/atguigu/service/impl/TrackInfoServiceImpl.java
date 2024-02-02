package com.atguigu.service.impl;

import com.atguigu.constant.SystemConstant;
import com.atguigu.entity.AlbumInfo;
import com.atguigu.entity.TrackInfo;
import com.atguigu.entity.TrackStat;
import com.atguigu.mapper.TrackInfoMapper;
import com.atguigu.service.AlbumInfoService;
import com.atguigu.service.TrackInfoService;
import com.atguigu.service.TrackStatService;
import com.atguigu.service.VodService;
import com.atguigu.util.AuthContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 声音信息 服务实现类
 * </p>
 *
 * @author 亨亨sama
 * @since 2024-01-29
 */
@Service
public class TrackInfoServiceImpl extends ServiceImpl<TrackInfoMapper, TrackInfo> implements TrackInfoService {
    @Autowired
    private AlbumInfoService albumInfoService;
    @Autowired
    private TrackStatService trackStatService;
    @Autowired
    private VodService vodService;
    /**
     * 新增声音
     * @param trackInfo
     */
    @Override
    public void saveTrackInfo(TrackInfo trackInfo) {
//        保存声音的基本信息,看上传的数据中哪些在trackInfo中没有,没有就需要单独添加
        trackInfo.setUserId(AuthContextHolder.getUserId());
        trackInfo.setStatus(SystemConstant.TRACK_APPROVED);
//        查询专辑中声音编号最大的值,这里是上传每个声音前面的编号
        LambdaQueryWrapper<TrackInfo> wrapper = new LambdaQueryWrapper<>();
//        比较上传的声音的专辑id是否和本来的专辑id相同
        wrapper.eq(TrackInfo::getAlbumId, trackInfo.getAlbumId());
//        拿到id之后进行排序,升序降序都可以,这里使用升序取最后一个,即最大值
        wrapper.orderByAsc(TrackInfo::getOrderNum);
//        通过调用getOrderNum得到结果进行查询
        wrapper.select(TrackInfo::getOrderNum);
//        把最后一个sql的结果取出来，即取到最大值
        wrapper.last("limit 1");
//        getone只能取一条数据，它会返回一个实例对象。
//        所以wrapper是多条数据会报错，一般和.last(“limit 1”)一起使用
        TrackInfo maxOrderNoTrackInfo = getOne(wrapper);
        int orderNum = 1;
        if (maxOrderNoTrackInfo != null) {
            orderNum = maxOrderNoTrackInfo.getOrderNum() + 1;
        }
        trackInfo.setOrderNum(orderNum);
//        保存声音的基本信息
        save(trackInfo);

//        更新专辑的声音个数
//        通过声音表获取到专辑id，通过id查询到专辑
        AlbumInfo albumInfo = albumInfoService.getById(trackInfo.getAlbumId());
        int includeTrackCount = albumInfo.getIncludeTrackCount() + 1;
        albumInfo.setIncludeTrackCount(includeTrackCount);
        albumInfoService.updateById(albumInfo);

//        初始化声音的统计信息
//        这里的逻辑顺序是,先通过方法调用id和类型,在下面生成的私有方法中,添加属性,后面在把属性添加到集合中
//        再创建新的方法,把最开始调用的方法放入,且根据需要的类型,分别调用封装的方法,最后返回一个集合,再再最外层,把这个的所有数据保存到表中
        List<TrackStat> trackStatList = buildTrackStat(trackInfo.getId());
        trackStatService.saveBatch(trackStatList);
    }


    private List<TrackStat> buildTrackStat(Long trackId) {
        ArrayList<TrackStat> trackStatArrayList = new ArrayList<>();
        initTrackStat(trackId, trackStatArrayList,SystemConstant.PLAY_NUM_TRACK);
        initTrackStat(trackId, trackStatArrayList,SystemConstant.SUBSCRIBE_NUM_ALBUM);
        initTrackStat(trackId, trackStatArrayList,SystemConstant.BUY_NUM_ALBUM);
        initTrackStat(trackId, trackStatArrayList,SystemConstant.COMMENT_NUM_TRACK);
        return trackStatArrayList;
    }

//
    private void initTrackStat(Long trackId, ArrayList<TrackStat> trackStatArrayList, String statType) {
        TrackStat trackStat = new TrackStat();
        trackStat.setTrackId(trackId);
        trackStat.setStatType(statType);
        trackStat.setStatNum(0);
//        在单独创建一个数组,把trackstat放进去
        trackStatArrayList.add(trackStat);
    }

    /**
     * 更新声音
     * @param trackInfo
     */
    @Override
    public void updateTrackInfoById(TrackInfo trackInfo) {
//        直接调用修改声音的方法
        vodService.getTrackMediaInfo(trackInfo);
//        再通过updateById方法,把修改后的trackInfo上传表
        updateById(trackInfo);
    }

    /**
     * 通过id删除声音
     * @param trackId
     */
    @Override
    public void deleteTrackInfo(Long trackId) {
//        更新专辑的声音个数
        TrackInfo trackInfo = getById(trackId);
        AlbumInfo albumInfo = albumInfoService.getById(trackInfo.getAlbumId());
        albumInfo.setIncludeTrackCount(albumInfo.getIncludeTrackCount()-1);
        albumInfoService.updateAlbumInfo(albumInfo);
//        调用腾讯云声音
        vodService.removeTrack(trackInfo.getMediaFileId());
//        删除统计信息
        trackStatService.remove(new LambdaQueryWrapper<TrackStat>().eq(TrackStat::getTrackId,trackId));
    }
}

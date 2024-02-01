package com.atguigu.service.impl;

import com.atguigu.constant.SystemConstant;
import com.atguigu.entity.AlbumAttributeValue;
import com.atguigu.entity.AlbumInfo;
import com.atguigu.entity.AlbumStat;
import com.atguigu.mapper.AlbumInfoMapper;
import com.atguigu.service.AlbumAttributeValueService;
import com.atguigu.service.AlbumInfoService;
import com.atguigu.service.AlbumStatService;
import com.atguigu.util.AuthContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 专辑信息 服务实现类
 * </p>
 *
 * @author 亨亨sama
 * @since 2024-01-29
 */
@Service
public class AlbumInfoServiceImpl extends ServiceImpl<AlbumInfoMapper, AlbumInfo> implements AlbumInfoService {

    @Autowired
    private AlbumAttributeValueService albumAttributeValueService;
    @Autowired
    private AlbumStatService albumStatService;

    /**
     * 保存专辑信息
     * @param albumInfo
     */
    @Override
    public void saveAlbumInfo(AlbumInfo albumInfo) {
//        添加专辑id,专辑状态
        Long userId = AuthContextHolder.getUserId();
        albumInfo.setUserId(userId);
        albumInfo.setStatus(SystemConstant.ALBUM_APPROVED);
//        所有专辑还需要有免费试听的集数,比如设定试听为3集,通过比较付费类型的数值是否相同,来判断是收费还是免费的
        if (!SystemConstant.FREE_ALBUM.equals(albumInfo.getPayType())) {
//            不是免费的,就设定试听集数
            albumInfo.setTracksForFree(3);
        }
        save(albumInfo);

//        保存专辑的属性信息,上面是专辑的基本信息
        List<AlbumAttributeValue> albumPropertyValueList = albumInfo.getAlbumPropertyValueList();
        if (!CollectionUtils.isEmpty(albumPropertyValueList)) {
            for (AlbumAttributeValue albumAttributeValue : albumPropertyValueList) {
//                设置专辑id
                albumAttributeValue.setAlbumId(albumInfo.getId());
            }
//            把集合保存,不能在遍历中保存,因为一次遍历就会一次保存,严重影响性能
//            保存专辑的标签属性
            albumAttributeValueService.saveBatch(albumPropertyValueList);
        }
//        保存专辑的数据信息
        List<AlbumStat> albumStatList = buildAlbumStat(albumInfo.getId());
        albumStatService.saveBatch(albumStatList);
//        初始化统计数据
    }
    private List<AlbumStat> buildAlbumStat(Long albumId) {
        ArrayList<AlbumStat> albumStatArrayList = new ArrayList<>();
        initAlbumStat(albumId,albumStatArrayList, SystemConstant.PLAY_NUM_ALBUM);
        initAlbumStat(albumId,albumStatArrayList,SystemConstant.SUBSCRIBE_NUM_ALBUM);
        initAlbumStat(albumId,albumStatArrayList,SystemConstant.BUY_NUM_ALBUM);
        initAlbumStat(albumId,albumStatArrayList,SystemConstant.COMMENT_NUM_ALBUM);
        return albumStatArrayList;
    }

    private void initAlbumStat(Long albumId, ArrayList<AlbumStat> albumStatArrayList, String statType) {
        AlbumStat albumStat = new AlbumStat();
        albumStat.setAlbumId(albumId);
        albumStat.setStatType(statType);
        albumStat.setStatNum(0);
    }

    /**
     * 根据id查询专辑
     * @param albumId
     * @return
     */
    @Override
    public AlbumInfo getAlbumInfoById(Long albumId) {
        AlbumInfo albumInfo = getById(albumId);
        LambdaQueryWrapper<AlbumAttributeValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlbumAttributeValue::getAlbumId, albumId);
        List<AlbumAttributeValue> albumAttributeValueList = albumAttributeValueService.list(wrapper);
        albumInfo.setAlbumPropertyValueList(albumAttributeValueList);
        return albumInfo;
    }

    /**
     * 更新专辑
     * @param albumInfo
     */
    @Override
    public void updateAlbumInfo(AlbumInfo albumInfo) {
//        调用修改方法,把修改的数据更新
        updateById(albumInfo);
//       删除原本的标签
        LambdaQueryWrapper<AlbumAttributeValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlbumAttributeValue::getAlbumId, albumInfo.getId());
        albumAttributeValueService.remove(wrapper);
//        重新保存标签信息
        List<AlbumAttributeValue> albumPropertyValueList = albumInfo.getAlbumPropertyValueList();
        if (!CollectionUtils.isEmpty(albumPropertyValueList)) {
            for (AlbumAttributeValue albumAttributeValue : albumPropertyValueList) {
                albumAttributeValue.setAlbumId(albumInfo.getId());
            }
//            保存标签属性
            albumAttributeValueService.saveBatch(albumPropertyValueList);
        }
    }

    /**
     * 删除专辑
     * @param albumId
     */
    @Override
    public void deleteAlbumInfo(Long albumId) {
        removeById(albumId);
//        删除专辑的属性值
        LambdaQueryWrapper<AlbumAttributeValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlbumAttributeValue::getAlbumId, albumId);
        albumAttributeValueService.remove(wrapper);

//        删除统计属性
        LambdaQueryWrapper<AlbumStat> statWrapper = new LambdaQueryWrapper<>();
        statWrapper.eq(AlbumStat::getAlbumId, albumId);
        albumStatService.remove(statWrapper);
    }
}

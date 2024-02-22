package com.atguigu.service.impl;

import com.atguigu.entity.BaseCategory2;
import com.atguigu.entity.BaseCategory3;
import com.atguigu.mapper.BaseCategory3Mapper;
import com.atguigu.service.BaseCategory2Service;
import com.atguigu.service.BaseCategory3Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 三级分类表 服务实现类
 * </p>
 *
 * @author 亨亨sama
 * @since 2024-01-29
 */
@Service
public class BaseCategory3ServiceImpl extends ServiceImpl<BaseCategory3Mapper, BaseCategory3> implements BaseCategory3Service {

    /**
     * 通过一级id找到三级分类
     *
     * @param category1Id
     * @return
     */

//    注入要使用使用的二级和三级的表的service
    @Autowired
    private BaseCategory2Service category2Service;
    @Autowired
    private BaseCategory3Service category3Service;
    @Override
    public List<BaseCategory3> getCategory3ListByCategory1Id(Long category1Id) {
//        使用mybatis-plus
        LambdaQueryWrapper<BaseCategory2> wrapper = new LambdaQueryWrapper<>();
//       等值查询，即二级列表的category1id等于category1id
        wrapper.eq(BaseCategory2::getCategory1Id, category1Id);
//        查询它的id字段
        wrapper.select(BaseCategory2::getId);
//        通过找到的二级分类id进行查询，找到对应的二级分类列表
        List<BaseCategory2> category2List = category2Service.list(wrapper);


//        category2List.stream().map(BaseCategory2::getId).forEach(category2Id -> {
////            通过二级分类id找到三级分类
//            LambdaQueryWrapper<BaseCategory3> wrapper1 = new LambdaQueryWrapper<>();
//            wrapper1.eq(BaseCategory3::getCategory2Id, category2Id);
//            List<BaseCategory3> category3List = category3Service.list(wrapper1);
//        });
//        return category3List;
        //        通过stream流的方法把二级分类的所有id都找到，收集成为集合，并存在list中
        List<Long> category2IdList = category2List.stream().map(BaseCategory2::getId).collect(Collectors.toList());
//        使用mybatis-plus
        LambdaQueryWrapper<BaseCategory3> wrapper1 = new LambdaQueryWrapper<>();
//        把category3表中的所有Category2Id，根据整理出来的category2IdList里面的id相同的找到，把二级分类的id放到in里面，
        wrapper1.in(BaseCategory3::getCategory2Id, category2IdList);
//        根据业务要求，根据istop排序（表中的数据能看出来，这里是因为数据很多，总要有对应的数据进行排序），并展示7个数据，这个根据页面情况去设置
        wrapper1.eq(BaseCategory3::getIsTop, 1).last("limit 7");
//        返回找到的三级分类列表
        List<BaseCategory3> category3List = category3Service.list(wrapper1);
        return category3List;
    }
}

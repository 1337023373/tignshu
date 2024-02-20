package com.atguigu.service.impl;

import com.atguigu.entity.BaseCategoryView;
import com.atguigu.mapper.BaseCategoryViewMapper;
import com.atguigu.result.RetVal;
import com.atguigu.service.BaseCategoryViewService;
import com.atguigu.vo.CategoryVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * VIEW 服务实现类
 * </p>
 *
 * @author 亨亨sama
 * @since 2024-01-29
 */
@Service
public class BaseCategoryViewServiceImpl extends ServiceImpl<BaseCategoryViewMapper, BaseCategoryView> implements BaseCategoryViewService {

//    @Override
//    public RetVal getAllCategoryList() {
//        //a.查询所有的分类信息
//        List<BaseCategoryView> allCategoryList = list();
//        //        b.找到所有的一级分类
////        通过stream流的形式对集合遍历,并根据id进行分组
//        Map<Long, List<BaseCategoryView>> category1map = allCategoryList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
////        迭代category1map,根据前端需要的数据类型样式,进行迭代,并创建对应的实体类对数据进行接收
////        把上面得到的map结合,通过entryset方法,拿到包含所有的键值对的set集合,再遍历它,得到的category1Entry,就是每一个实例对象
// //       for (Map.Entry<Long, List<BaseCategoryView>> category1Entry : category1map.entrySet()) {
////        用map写返回category1vo
//        List<Object> categoryVoList = category1map.entrySet().stream().map(category1Entry -> {
////          key对应id
//            Long category1Id = category1Entry.getKey();
////            value对应
//            List<BaseCategoryView> category1List = category1Entry.getValue();
//            CategoryVo category1Vo = new CategoryVo();
//            category1Vo.setCategoryId(category1Id);
////            这里的0,是因为BaseCategoryView中的数据大多是重复的,所以直接那第一个值,从里面去数据
//            category1Vo.setCategoryName(category1List.get(0).getCategory1Name());
//
////            找到所有的二级分类
//            Map<Long, List<BaseCategoryView>> category2map = allCategoryList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
////          这里需要有返回值给上面的child,所以直接用map
//            List<CategoryVo> category1Children = category2map.entrySet().stream().map(category2Entry -> {
//                Long category2Id = category1Entry.getKey();
//                List<BaseCategoryView> category2List = category2Entry.getValue();
//                CategoryVo category2Vo = new CategoryVo();
//                category2Vo.setCategoryId(category2Id);
////            这里的0,是因为BaseCategoryView中的数据大多是重复的,所以直接那第一个值,从里面去数据
//                category2Vo.setCategoryName(category2List.get(0).getCategory2Name());
//                //            找到所有的三级分类
//                Map<Long, List<BaseCategoryView>> category3map = allCategoryList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory3Id));
//                List<CategoryVo> category2Children = category3map.entrySet().stream().map(category3Entry -> {
//                    Long category3Id = category1Entry.getKey();
//                    List<BaseCategoryView> category3List = category3Entry.getValue();
//                    CategoryVo category3Vo = new CategoryVo();
//                    category3Vo.setCategoryId(category3Id);
////            这里的0,是因为BaseCategoryView中的数据大多是重复的,所以直接那第一个值,从里面去数据
//                    category3Vo.setCategoryName(category3List.get(0).getCategory3Name());
//                    category3Vo.setCategoryChild(null);
//                    return category3Vo;
//                }).collect(Collectors.toList());
//                category2Vo.setCategoryChild(category2Children);
//                return category2Vo;
//            }).collect(Collectors.toList());
//            category1Vo.setCategoryChild(category1Children);
//            return category1Vo;
//        }).collect(Collectors.toList());
//        return RetVal.ok(categoryVoList);
//    }
    @Override
    public RetVal getAllCategoryList() {
       List<CategoryVo> categoryVoList =  baseMapper.getAllCategoryList();
        return RetVal.ok(categoryVoList);
    }

}

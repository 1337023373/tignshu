package com.atguigu.mapper;

import com.atguigu.entity.BaseCategoryView;
import com.atguigu.vo.CategoryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * VIEW Mapper 接口
 * </p>
 *
 * @author 亨亨sama
 * @since 2024-01-29
 */
public interface BaseCategoryViewMapper extends BaseMapper<BaseCategoryView> {

    List<CategoryVo> getAllCategoryList();
}

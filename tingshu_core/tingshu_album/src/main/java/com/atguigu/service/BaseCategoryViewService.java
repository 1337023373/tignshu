package com.atguigu.service;

import com.atguigu.entity.BaseCategoryView;
import com.atguigu.result.RetVal;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * VIEW 服务类
 * </p>
 *
 * @author 亨亨sama
 * @since 2024-01-29
 */
public interface BaseCategoryViewService extends IService<BaseCategoryView> {

    RetVal getAllCategoryList();
}

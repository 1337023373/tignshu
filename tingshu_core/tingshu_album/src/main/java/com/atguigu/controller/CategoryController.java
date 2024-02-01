package com.atguigu.controller;

import com.atguigu.entity.BaseAttribute;
import com.atguigu.mapper.BaseAttributeMapper;
import com.atguigu.result.RetVal;
import com.atguigu.service.BaseCategoryViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 一级分类表 前端控制器
 * </p>
 *
 * @author 亨亨sama
 * @since 2024-01-29
 */

@Tag(name = "分类管理")
@RestController
@RequestMapping("/api/album/category")
///api/album/category/getAllCategoryList
public class CategoryController {

    @Autowired
    private BaseCategoryViewService categoryViewService;

    @Autowired
    private BaseAttributeMapper baseAttributeMapper;
    //@TingShuLogin
    @Operation(summary = "获取全部分类信息")
    @GetMapping("/getAllCategoryList")
    public RetVal getAllCategoryList() {

        return categoryViewService.getAllCategoryList();
    }

    @Operation(summary = "根据一级id查询专辑分类信息")
    @GetMapping("/getPropertyByCategory1Id/{category1Id}")
    public RetVal getPropertyByCategory1Id(@PathVariable long category1Id) {
        List<BaseAttribute> baseAttributeList = baseAttributeMapper.getPropertyByCategory1Id(category1Id);
        return RetVal.ok(baseAttributeList);
    }
}

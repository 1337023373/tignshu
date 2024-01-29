package com.atguigu.controller;

import com.atguigu.login.TingShuLogin;
import com.atguigu.result.RetVal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @TingShuLogin
    @Operation(summary = "获取全部分类信息")
    @GetMapping("/getAllCategoryList")
    public RetVal getAllCategoryList() {

        return RetVal.ok();
    }
}

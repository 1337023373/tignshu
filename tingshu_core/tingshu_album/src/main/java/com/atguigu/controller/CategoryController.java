package com.atguigu.controller;

import com.atguigu.entity.BaseAttribute;
import com.atguigu.entity.BaseCategory3;
import com.atguigu.entity.BaseCategoryView;
import com.atguigu.mapper.BaseAttributeMapper;
import com.atguigu.result.RetVal;
import com.atguigu.service.BaseCategory3Service;
import com.atguigu.service.BaseCategoryViewService;
import com.atguigu.vo.CategoryVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
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
    @Autowired
    private BaseCategory3Service category3Service;

    //@TingShuLogin
    @Operation(summary = "获取全部分类信息")
    @GetMapping("/getAllCategoryList")
    public RetVal getAllCategoryList() {

        List<CategoryVo> allCategoryList = categoryViewService.getAllCategoryList(null);
        return RetVal.ok(allCategoryList);
    }

    @Operation(summary = "根据一级id查询专辑分类信息")
    @GetMapping("/getPropertyByCategory1Id/{category1Id}")
    public RetVal getPropertyByCategory1Id(@PathVariable long category1Id) {
        List<BaseAttribute> baseAttributeList = baseAttributeMapper.getPropertyByCategory1Id(category1Id);
        return RetVal.ok(baseAttributeList);
    }

    /**
     * 搜索模块所使用
     */
    @Operation(summary = "根据三级id查询专辑分类信息")
    @GetMapping("/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable Long category3Id) {
        return categoryViewService.getById(category3Id);
    }

    //    album/category/getCategory3ListByCategory1Id/2
//    实现原理是通过一级id找二级,再找三级,所以肯定最后查三级表,所以注入三级的service,通过表可知,返回的值不止一个,所以直接用list接收
    @Operation(summary = "根据一级id查询三级id信息")
    @GetMapping("/getCategory3ListByCategory1Id/{category1Id}")
    public RetVal<List<BaseCategory3>> getCategory3ListByCategory1Id(@PathVariable Long category1Id) {
        List<BaseCategory3> category3List = category3Service.getCategory3ListByCategory1Id(category1Id);
        return RetVal.ok(category3List);
    }


//    这个代码实际上在上面已经写过了，上面是通过三级遍历拿到三级列表，没有传递参数，而我们这个方法需要传递参数，所以在方法上稍微加上id
//    上面不需要id的给null，然后写一个动态sql，把id嵌入即可完成
    @Operation(summary = "根据一级id查询子分类信息")
    @GetMapping("/getCategoryByCategory1Id/{category1Id}")
    public RetVal getCategoryByCategory1Id(@PathVariable Long category1Id) {
        List<CategoryVo> categoryList = categoryViewService.getAllCategoryList(category1Id);
//        判断是否为空
        if (!CollectionUtils.isEmpty(categoryList)) {
            return RetVal.ok(categoryList.get(0));
        }
        return RetVal.ok();
    }
}

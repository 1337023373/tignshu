package com.atguigu;

import com.atguigu.entity.BaseCategory1;
import com.atguigu.entity.BaseCategory3;
import com.atguigu.entity.BaseCategoryView;
import com.atguigu.result.RetVal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

//@FeignClient(value = "tingshu-album",fallback = AlbumFeignClientImpl.class)
//创建远程链接
@FeignClient(value = "tingshu-album")
public interface CategoryFeignClient {

    //根据三级分类id获取分类信息的远程调用
    @GetMapping("/api/album/category/getCategoryView/{category3Id}")
    BaseCategoryView getCategoryView(@PathVariable Long category3Id);

//    根据一级id查到三级分类信息的远程调用
    @GetMapping("/api/album/category/getCategory3ListByCategory1Id/{category1Id}")
    public RetVal<List<BaseCategory3>> getCategory3ListByCategory1Id(@PathVariable Long category1Id);

//    查询一级分类信息的远程调用
    @GetMapping("/api/album/category/getCategory1")
    public List<BaseCategory1> getCategory1();
}

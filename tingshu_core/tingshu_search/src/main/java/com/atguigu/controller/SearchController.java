package com.atguigu.controller;

import com.atguigu.constant.RedisConstant;
import com.atguigu.entity.AlbumInfoIndex;
import com.atguigu.query.AlbumIndexQuery;
import com.atguigu.result.RetVal;
import com.atguigu.service.SearchService;
import com.atguigu.vo.AlbumSearchResponseVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Tag(name = "搜索专辑管理")
@RestController
@RequestMapping("/api/search/albumInfo")
public class SearchController {
    @Autowired
    private SearchService searchService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Operation(summary = "上架专辑")
    @GetMapping("/onSaleAlbum/{albumId}")
    public void onSaleAlbum(@PathVariable Long albumId) {
        searchService.onSaleAlbum(albumId);
    }

    @Operation(summary = "批量上架专辑")
    @GetMapping("/batchOnSaleAlbum")

    public RetVal batchOnSaleAlbum() {
//  这里准确的写法是，从数据库中拿到最大的专辑id值,进行循环添加上架专辑
        for (long i = 1; i <= 1577; i++) {
            searchService.onSaleAlbum(i);
        }
        return RetVal.ok();
    }

    @Operation(summary = "下架专辑")
    @GetMapping("/offSaleAlbum/{albumId}")

    public void offSaleAlbum(@PathVariable Long albumId) {
        searchService.offSaleAlbum(albumId);
    }

    //    /api/search/albumInfo/getChannelData/7
    @Operation(summary = "获取主页频道数据")
    @GetMapping("/getChannelData/{category1Id}")
    public RetVal getChannelData(@PathVariable Long category1Id) {
        List<Map<Object, Object>> channelData = searchService.getChannelData(category1Id);
        return RetVal.ok(channelData);
    }

    //    http://127.0.0.1/api/search/albumInfo/autoCompleteSuggest/as
    @Operation(summary = "搜索自动补全")
    @GetMapping("/autoCompleteSuggest/{keyword}")
    public RetVal autoCompleteSuggest(@PathVariable String keyword) {
        Set<String> suggestSet = searchService.autoCompleteSuggest(keyword);
        return RetVal.ok(suggestSet);
    }

    @Operation(summary = "搜索")
    @PostMapping
    public RetVal search(@RequestBody AlbumIndexQuery albumIndexQuery) {
        AlbumSearchResponseVo responseVo = searchService.search(albumIndexQuery);
        return RetVal.ok(responseVo);
    }

    @Operation(summary = "更新排行榜列表")
    @GetMapping("/updateRanking")
    public RetVal updateRanking() {
        searchService.updateRanking();
        return RetVal.ok();
    }

    @Operation(summary = "获取排行榜列表")
    @GetMapping("/getRankingList/{category1Id}/{rankingType}")
    public RetVal getRankingList(@PathVariable Long category1Id, @PathVariable String rankingType) {
//        从redis中获取排行榜列表
        List<AlbumInfoIndex> albumList = (List<AlbumInfoIndex>) redisTemplate.boundHashOps(RedisConstant.RANKING_KEY_PREFIX + category1Id).get(rankingType);
        return RetVal.ok(albumList);
    }

    //    http://127.0.0.1/api/search/albumInfo/getAlbumDetail/125
    @Operation(summary = "根据专辑id获取详细信息")
    @GetMapping("/getAlbumDetail/{albumId}")
    public RetVal getAlbumDetail(@PathVariable Long albumId) {
        HashMap<String, Object> albumDetail = searchService.getAlbumDetail(albumId);
        return RetVal.ok(albumDetail);
    }
}

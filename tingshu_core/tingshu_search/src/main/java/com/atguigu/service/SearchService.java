package com.atguigu.service;

import com.atguigu.query.AlbumIndexQuery;
import com.atguigu.vo.AlbumSearchResponseVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SearchService {
    void onSaleAlbum(Long albumId);

    void offSaleAlbum(Long albumId);

    List<Map<Object, Object>> getChannelData(Long category1Id);

    Set<String> autoCompleteSuggest(String keyword);

    AlbumSearchResponseVo search(AlbumIndexQuery albumIndexQuery);

    void updateRanking();

    HashMap<String, Object> getAlbumDetail(Long albumId);
}

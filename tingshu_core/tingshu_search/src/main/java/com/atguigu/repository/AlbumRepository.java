package com.atguigu.repository;

import com.atguigu.entity.AlbumInfoIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AlbumRepository extends ElasticsearchRepository<AlbumInfoIndex,Long> {
}

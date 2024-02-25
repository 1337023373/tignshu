package com.atguigu.repository;

import com.atguigu.entity.SuggestIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SuggestRepository extends ElasticsearchRepository<SuggestIndex,Long> {
}

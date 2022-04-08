package com.marco.bilibili.dao.repository;

import com.marco.bilibili.domain.Video;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface VideoRepository extends ElasticsearchRepository<Video, Long> {

    //根据关键词模糊查询(现在写的只能查询title区域的)
    Video findByTitleLike(String keyword);

}

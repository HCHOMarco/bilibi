package com.marco.bilibili.dao.repository;

import com.marco.bilibili.domain.UserInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserInfoRepository extends ElasticsearchRepository<UserInfo, Long> {

}

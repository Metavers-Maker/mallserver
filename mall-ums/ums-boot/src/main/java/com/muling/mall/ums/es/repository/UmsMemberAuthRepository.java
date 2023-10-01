package com.muling.mall.ums.es.repository;

import com.muling.mall.ums.es.entity.UmsMemberAuth;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UmsMemberAuthRepository extends ElasticsearchRepository<UmsMemberAuth, Long> {

}

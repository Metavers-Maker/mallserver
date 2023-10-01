package com.muling.mall.ums.es.repository;

import com.muling.mall.ums.es.entity.UmsMember;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UmsMemberRepository extends ElasticsearchRepository<UmsMember, Long> {

}

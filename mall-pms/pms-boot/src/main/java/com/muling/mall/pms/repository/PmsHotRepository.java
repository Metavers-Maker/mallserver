package com.muling.mall.pms.repository;

import com.muling.mall.pms.es.entity.PmsHot;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PmsHotRepository extends ElasticsearchRepository<PmsHot, Long> {

}

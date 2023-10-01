package com.muling.mall.pms.repository;

import com.muling.mall.pms.es.entity.PmsGround;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PmsGroundRepository extends ElasticsearchRepository<PmsGround, Long> {

}

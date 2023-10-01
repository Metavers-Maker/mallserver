package com.muling.mall.pms.repository;

import com.muling.mall.pms.es.entity.PmsSpu;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PmsSpuRepository extends ElasticsearchRepository<PmsSpu, Long> {

}

package com.muling.mall.pms.repository;

import com.muling.mall.pms.es.entity.PmsBrand;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PmsBrandRepository extends ElasticsearchRepository<PmsBrand, Long> {

}

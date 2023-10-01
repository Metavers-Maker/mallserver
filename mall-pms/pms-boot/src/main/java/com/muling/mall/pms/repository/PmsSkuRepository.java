package com.muling.mall.pms.repository;

import com.muling.mall.pms.es.entity.PmsSku;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PmsSkuRepository extends ElasticsearchRepository<PmsSku, Long> {

}

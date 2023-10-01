package com.muling.mall.pms.repository;

import com.muling.mall.pms.es.entity.PmsBanner;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PmsBannerRepository extends ElasticsearchRepository<PmsBanner, Long> {

}

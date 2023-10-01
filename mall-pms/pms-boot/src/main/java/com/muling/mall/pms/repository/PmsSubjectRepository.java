package com.muling.mall.pms.repository;

import com.muling.mall.pms.es.entity.PmsSubject;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PmsSubjectRepository extends ElasticsearchRepository<PmsSubject, Long> {

}

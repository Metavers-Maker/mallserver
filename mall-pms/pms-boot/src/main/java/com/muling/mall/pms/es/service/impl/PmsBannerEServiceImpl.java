package com.muling.mall.pms.es.service.impl;


import com.muling.mall.pms.common.enums.ViewTypeEnum;
import com.muling.mall.pms.converter.BannerConverter;
import com.muling.mall.pms.es.entity.PmsBanner;
import com.muling.mall.pms.es.service.PmsBannerEService;
import com.muling.mall.pms.pojo.vo.BannerVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PmsBannerEServiceImpl implements PmsBannerEService {

    private final ElasticsearchRestTemplate elasticsearchRestTemple;

    @Override
    public List<BannerVO> getBannerVisibleLists() {

        //条件
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("visible", ViewTypeEnum.VISIBLE.getValue());
        //排序
        FieldSortBuilder sortBuilder = new FieldSortBuilder("updated").order(SortOrder.DESC);
        //搜索
        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(queryBuilder).withSorts(sortBuilder).build();
        SearchHits<PmsBanner> hits = elasticsearchRestTemple.search(query, PmsBanner.class);
        List<PmsBanner> list = hits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
        log.info("热播 list = {}", list);
        List<BannerVO> result = BannerConverter.INSTANCE.bannersToVoList(list);

        return result;
    }
}

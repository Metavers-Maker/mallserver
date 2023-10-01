package com.muling.mall.pms.es.service.impl;


import com.muling.mall.pms.common.enums.ViewTypeEnum;
import com.muling.mall.pms.converter.HotConverter;
import com.muling.mall.pms.es.entity.PmsHot;
import com.muling.mall.pms.es.service.PmsHotEService;
import com.muling.mall.pms.pojo.vo.HotVO;
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
public class PmsHotEServiceImpl implements PmsHotEService {

    private final ElasticsearchRestTemplate elasticsearchRestTemple;

    public List<HotVO> getLists() {
        //条件
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("visible", ViewTypeEnum.VISIBLE.getValue());
        //排序
        FieldSortBuilder sortBuilder = new FieldSortBuilder("updated").order(SortOrder.DESC);
        //搜索
        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(queryBuilder).withSorts(sortBuilder).build();
        SearchHits<PmsHot> hits = elasticsearchRestTemple.search(query, PmsHot.class);
        List<PmsHot> list = hits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
        log.info("热播 list = {}", list);
        List<HotVO> result = HotConverter.INSTANCE.hotsToVoList(list);
        return result;
    }


}

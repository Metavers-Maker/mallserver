package com.muling.mall.pms.es.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import com.muling.mall.pms.converter.GroundConverter;
import com.muling.mall.pms.es.entity.PmsGround;
import com.muling.mall.pms.es.service.PmsGroundEService;
import com.muling.mall.pms.pojo.query.GroundPageQuery;
import com.muling.mall.pms.pojo.vo.GroundVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class PmsGroundEServiceImpl implements PmsGroundEService {

    private final ElasticsearchRestTemplate elasticsearchRestTemple;


    public IPage<GroundVO> page(GroundPageQuery queryParams) {

        int pageNum = (int) queryParams.getPageNum();
        int pageSize = (int) queryParams.getPageSize();

        //条件
        BoolQueryBuilder queryBuilder = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.termQuery("type", queryParams.getType()))
                .must(QueryBuilders.termQuery("visible", ViewTypeEnum.VISIBLE.getValue()));

        //排序
        FieldSortBuilder sortBuilder = new FieldSortBuilder("sort").order(SortOrder.ASC);
        //分页
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        //搜索
        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(queryBuilder).withSorts(sortBuilder).withPageable(pageable).build();
        SearchHits<PmsGround> hits = elasticsearchRestTemple.search(query, PmsGround.class);

        long totalHits = hits.getTotalHits();
        long length = hits.getSearchHits().size();

        log.info("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);
        List<PmsGround> list = hits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
        List<GroundVO> result = GroundConverter.INSTANCE.grounds2VoList(list);
        Page<GroundVO> page = Page.of(pageNum, pageSize, totalHits);

        return page.setRecords(result);
    }

}

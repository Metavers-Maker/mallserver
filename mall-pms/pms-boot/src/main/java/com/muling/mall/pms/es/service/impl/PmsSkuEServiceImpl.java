package com.muling.mall.pms.es.service.impl;


import com.muling.mall.pms.common.enums.ViewTypeEnum;
import com.muling.mall.pms.converter.SkuConverter;
import com.muling.mall.pms.es.entity.PmsSku;
import com.muling.mall.pms.pojo.vo.SkuVO;
import com.muling.mall.pms.repository.PmsSkuRepository;
import com.muling.mall.pms.es.service.PmsSkuEService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PmsSkuEServiceImpl implements PmsSkuEService {

    private final ElasticsearchRestTemplate elasticsearchRestTemple;

    private final PmsSkuRepository skuRepository;

    @Cacheable(cacheNames = "pms", key = "'stock_num:'+#skuId")
    public Integer getStockNum(Long skuId) {
        Integer stockNum = 0;
        Optional<PmsSku> pmsSku = skuRepository.findById(skuId);
        if (pmsSku.isPresent()) {
            stockNum = pmsSku.get().getStockNum();
        }
        return stockNum;
    }

    @Override
    public List<SkuVO> getAppSkuDetails(List<Long> spuIds) {

        //条件
        BoolQueryBuilder queryBuilder = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.termsQuery("spu_id", spuIds))
                .must(QueryBuilders.termQuery("visible", ViewTypeEnum.VISIBLE.getValue().intValue()));
        //排序
        FieldSortBuilder sortBuilder = new FieldSortBuilder("_id").order(SortOrder.ASC);
        //搜索
        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(queryBuilder).withSorts(sortBuilder).build();
        SearchHits<PmsSku> hits = elasticsearchRestTemple.search(query, PmsSku.class);


        long totalHits = hits.getTotalHits();
        long length = hits.getSearchHits().size();

        log.info("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);
        List<PmsSku> list = hits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
        List<SkuVO> result = SkuConverter.INSTANCE.skus2VoList(list);

        return result;
    }

}

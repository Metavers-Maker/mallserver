package com.muling.mall.pms.es.service.impl;


import com.google.common.collect.Lists;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import com.muling.mall.pms.converter.SpuConverter;
import com.muling.mall.pms.es.entity.PmsSpu;
import com.muling.mall.pms.es.service.PmsSpuEService;
import com.muling.mall.pms.pojo.vo.GoodsPageVO;
import com.muling.mall.pms.repository.PmsSpuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
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
public class PmsSpuEServiceImpl implements PmsSpuEService {

    private final ElasticsearchRestTemplate elasticsearchRestTemple;

    private final PmsSpuRepository spuRepository;

    @Override
    public List<GoodsPageVO> listSpuBySubjectId(Long subjectId, Integer dev) {
        //条件
        BoolQueryBuilder queryBuilder = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.termQuery("subject_id", subjectId.longValue()))
                .must(QueryBuilders.termQuery("dev", dev))
                .must(QueryBuilders.termQuery("visible", ViewTypeEnum.VISIBLE.getValue().intValue()));

        //排序
        FieldSortBuilder sortBuilder = new FieldSortBuilder("sort").order(SortOrder.ASC);
        //搜索
        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(queryBuilder).withSorts(sortBuilder).build();
        SearchHits<PmsSpu> hits = elasticsearchRestTemple.search(query, PmsSpu.class);

        long totalHits = hits.getTotalHits();
        long length = hits.getSearchHits().size();

        log.info("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);
        List<PmsSpu> list = hits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
        List<GoodsPageVO> result = SpuConverter.INSTANCE.spus2VoList(list);
        return result;
    }

    @Override
    public GoodsPageVO getAppSpuDetail(Long spuId) {
        PmsSpu spu = spuRepository.findById(spuId).orElse(null);
        GoodsPageVO result = SpuConverter.INSTANCE.spu2Vo(spu);
        return result;
    }

    public List<GoodsPageVO> getAppSpuDetails(List<Long> spuIds) {
        Iterable<PmsSpu> iterable = spuRepository.findAllById(spuIds);
        List<PmsSpu> list = Lists.newArrayList(iterable);
        List<GoodsPageVO> result = SpuConverter.INSTANCE.spus2VoList(list);

        return result;
    }
}

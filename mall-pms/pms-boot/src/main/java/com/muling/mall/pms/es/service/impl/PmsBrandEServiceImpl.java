package com.muling.mall.pms.es.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import com.muling.mall.pms.converter.BrandConverter;
import com.muling.mall.pms.es.entity.PmsBrand;
import com.muling.mall.pms.es.service.PmsBrandEService;
import com.muling.mall.pms.pojo.query.BrandPageQuery;
import com.muling.mall.pms.pojo.query.SubjectPageQuery;
import com.muling.mall.pms.pojo.vo.BrandVO;
import com.muling.mall.pms.repository.PmsBrandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
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
public class PmsBrandEServiceImpl implements PmsBrandEService {

    private final ElasticsearchRestTemplate elasticsearchRestTemple;

    private final PmsBrandRepository brandRepository;

    @Override
    public List<BrandVO> getAppBrandDetails(List<Long> brandIds) {

        Iterable<PmsBrand> iterable = brandRepository.findAllById(brandIds);
        List<PmsBrand> list = Lists.newArrayList(iterable);
        log.info("查询到的品牌数量为：{}", list.size());
        List<BrandVO> result = BrandConverter.INSTANCE.brands2VoList(list);

        return result;
    }

    @Override
    public IPage<BrandVO> page(BrandPageQuery queryParams) {
        int pageNum = (int) queryParams.getPageNum();
        int pageSize = (int) queryParams.getPageSize();

        //条件
        BoolQueryBuilder queryBuilder = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.termQuery("visible", ViewTypeEnum.VISIBLE.getValue()));
        //排序
        FieldSortBuilder sortBuilder = new FieldSortBuilder("sort").order(SortOrder.ASC);
        //分页
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        //搜索
        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(queryBuilder).withSorts(sortBuilder).withPageable(pageable).build();
        SearchHits<PmsBrand> hits = elasticsearchRestTemple.search(query, PmsBrand.class);

        long totalHits = hits.getTotalHits();
        long length = hits.getSearchHits().size();

        log.info("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);
        List<PmsBrand> list = hits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
        List<BrandVO> result = BrandConverter.INSTANCE.brands2VoList(list);
        Page<BrandVO> page = Page.of(pageNum, pageSize, totalHits);

        return page.setRecords(result);
    }


    public void ss(SubjectPageQuery queryParams) {
        int pageNum = (int) queryParams.getPageNum();
        int pageSize = (int) queryParams.getPageSize();

        Iterable<PmsBrand> posts = brandRepository.findAll(PageRequest.of(pageNum, pageSize));

        //条件
        MatchAllQueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        //排序
        FieldSortBuilder sortBuilder = new FieldSortBuilder("id").order(SortOrder.ASC);
        //分页
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        //搜索
        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(queryBuilder).withPageable(pageable).build();
        SearchHits<PmsBrand> hits = elasticsearchRestTemple.search(query, PmsBrand.class);
        List<PmsBrand> list = hits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());

    }
}

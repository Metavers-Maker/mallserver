package com.muling.mall.bms.es.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.bms.converter.MemberItemConverter;
import com.muling.mall.bms.es.entity.OmsMemberItem;
import com.muling.mall.bms.es.service.IOmsMemberItemEService;
import com.muling.mall.bms.pojo.query.app.ItemPageQuery;
import com.muling.mall.bms.pojo.vo.app.MemberItemVO;
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
public class OmsMemberItemEServiceImpl implements IOmsMemberItemEService {

    private final ElasticsearchRestTemplate elasticsearchRestTemple;

    @Override
    public IPage<MemberItemVO> page(ItemPageQuery queryParams) {

        Long memberId = MemberUtils.getMemberId();

        int pageNum = (int) queryParams.getPageNum();
        int pageSize = (int) queryParams.getPageSize();

        //条件
        BoolQueryBuilder queryBuilder = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.termQuery("member_id", memberId))
                .must(QueryBuilders.termQuery("type", queryParams.getType()));
        //排序
        FieldSortBuilder sortBuilder = new FieldSortBuilder("created").order(SortOrder.DESC);
        //分页
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        //搜索
        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(queryBuilder).withSorts(sortBuilder).withPageable(pageable).build();
        SearchHits<OmsMemberItem> hits = elasticsearchRestTemple.search(query, OmsMemberItem.class);

        long totalHits = hits.getTotalHits();
        long length = hits.getSearchHits().size();

        log.info("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);
        List<OmsMemberItem> list = hits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
        List<MemberItemVO> result = MemberItemConverter.INSTANCE.items2voList(list);
        Page<MemberItemVO> page = Page.of(pageNum, pageSize, totalHits);

        return page.setRecords(result);
    }


}

package com.muling.mall.pms.es.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.muling.common.base.BasePageQuery;
import com.muling.common.constant.GlobalConstants;
import com.muling.mall.pms.converter.SubjectConverter;
import com.muling.mall.pms.es.entity.PmsSubject;
import com.muling.mall.pms.es.service.PmsSubjectEService;
import com.muling.mall.pms.pojo.vo.SubjectVO;
import com.muling.mall.pms.repository.PmsSubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.MatchQueryBuilder;
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
public class PmsSubjectEServiceImpl implements PmsSubjectEService {

    private final ElasticsearchRestTemplate elasticsearchRestTemple;

    private final PmsSubjectRepository subjectRepository;

    @Override
    public List<SubjectVO> getAppSubjectDetails(List<Long> subjectIds) {

        Iterable<PmsSubject> iterable = subjectRepository.findAllById(subjectIds);
        List<PmsSubject> list = Lists.newArrayList(iterable);
        List<SubjectVO> result = SubjectConverter.INSTANCE.subjects2VoList(list);

        return result;
    }


    public IPage<SubjectVO> page(BasePageQuery queryParams) {

        int pageNum = (int) queryParams.getPageNum();
        int pageSize = (int) queryParams.getPageSize();

        //条件
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("visible", GlobalConstants.STATUS_YES);
        //排序
        FieldSortBuilder sortBuilder = new FieldSortBuilder("sort").order(SortOrder.ASC);
        //分页
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        //搜索
        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(queryBuilder).withSorts(sortBuilder).withPageable(pageable).build();
        SearchHits<PmsSubject> hits = elasticsearchRestTemple.search(query, PmsSubject.class);

        long totalHits = hits.getTotalHits();
        long length = hits.getSearchHits().size();

        log.info("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);
        List<PmsSubject> list = hits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
        List<SubjectVO> result = SubjectConverter.INSTANCE.subjects2VoList(list);
        Page<SubjectVO> page = Page.of(pageNum, pageSize, totalHits);

        return page.setRecords(result);
    }

}

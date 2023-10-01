package com.muling.mall.pms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import com.muling.mall.pms.converter.HotConverter;
import com.muling.mall.pms.mapper.PmsHotMapper;
import com.muling.mall.pms.pojo.entity.PmsHot;
import com.muling.mall.pms.pojo.form.HotForm;
import com.muling.mall.pms.pojo.vo.HotVO;
import com.muling.mall.pms.repository.PmsHotRepository;
import com.muling.mall.pms.service.IPmsHotService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class PmsHotServiceImpl extends ServiceImpl<PmsHotMapper, PmsHot> implements IPmsHotService {

    private final PmsHotRepository hotRepository;

    private final ElasticsearchRestTemplate elasticsearchRestTemple;

    public List<HotVO> getLists() {
        LambdaQueryWrapper<PmsHot> wrapper = Wrappers.<PmsHot>lambdaQuery()
                .eq(PmsHot::getVisible, ViewTypeEnum.VISIBLE)
                .orderByDesc(PmsHot::getUpdated);
        List<PmsHot> list = list(wrapper);
        List<HotVO> result = HotConverter.INSTANCE.hotsToVOs(list);
        return result;
    }


    public void ss(){

        //条件
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("visible", ViewTypeEnum.VISIBLE.getValue());
        //排序
        FieldSortBuilder sortBuilder = new FieldSortBuilder("updated").order(SortOrder.DESC);
        //搜索
        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(queryBuilder).withSorts(sortBuilder).build();
        SearchHits<com.muling.mall.pms.es.entity.PmsHot> hits = elasticsearchRestTemple.search(query, com.muling.mall.pms.es.entity.PmsHot.class);
        List<com.muling.mall.pms.es.entity.PmsHot> list = hits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());

    }

    @Override
    public boolean save(HotForm hotForm) {
        PmsHot hot = HotConverter.INSTANCE.form2Po(hotForm);
        boolean b = this.save(hot);
        if (!b) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        return b;
    }

    @Override
    public boolean updateById(Long id, HotForm hotForm) {
        PmsHot hot = getById(id);
        if (hot == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST);
        }
        BeanUtil.copyProperties(hotForm, hot);

        return updateById(hot);
    }
}

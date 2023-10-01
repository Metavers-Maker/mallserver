package com.muling.global.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.common.enums.VisibleEnum;
import com.muling.global.pojo.entity.News;
import com.muling.global.pojo.form.NewsForm;
import com.muling.global.pojo.query.NewsPageQuery;
import com.muling.global.pojo.vo.NewsVO;

import java.util.Collection;
import java.util.List;

public interface INewsService extends IService<News> {

    public IPage<NewsVO> page(NewsPageQuery queryParams);

    boolean add(NewsForm form);

    boolean update(Long id, NewsForm form);

    boolean update(Long id, VisibleEnum visible);

    public boolean delete(Collection<String> ids);

}

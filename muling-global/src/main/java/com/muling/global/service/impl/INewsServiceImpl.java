package com.muling.global.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.enums.VisibleEnum;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.global.converter.NewsConverter;
import com.muling.global.mapper.NewsMapper;
import com.muling.global.pojo.entity.News;
import com.muling.global.pojo.form.NewsForm;
import com.muling.global.pojo.query.NewsPageQuery;
import com.muling.global.pojo.vo.NewsVO;
import com.muling.global.service.INewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class INewsServiceImpl extends ServiceImpl<NewsMapper, News> implements INewsService {

    @Override
    public IPage<NewsVO> page(NewsPageQuery queryParams) {
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<News>()
                .eq(queryParams.getType() != null, News::getType, queryParams.getType())
                .eq(News::getVisible, VisibleEnum.DISPLAY)
                .orderByDesc(News::getSort)
                .orderByDesc(News::getCreated);
        Page<News> page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);
        Page<NewsVO> result = NewsConverter.INSTANCE.entity2PageVO(page);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean add(NewsForm form) {
        News news = NewsConverter.INSTANCE.form2po(form);
        return save(news);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(Long configId, NewsForm form) {
        News news = getById(configId);
        if (news == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST);
        }
        NewsConverter.INSTANCE.updatePo(form, news);
        return updateById(news);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(Long id, VisibleEnum visible) {
        boolean status = update(new LambdaUpdateWrapper<News>()
                .eq(News::getId, id)
                .set(News::getVisible, visible));
        return status;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(Collection<String> ids) {
        return removeByIds(ids);
    }
}

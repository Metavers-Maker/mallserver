package com.muling.global.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.global.pojo.entity.News;
import com.muling.global.pojo.form.NewsForm;
import com.muling.global.pojo.vo.NewsVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NewsConverter {

    NewsConverter INSTANCE = Mappers.getMapper(NewsConverter.class);

    News form2po(NewsForm form);

    void updatePo(NewsForm form, @MappingTarget News news);

    NewsVO po2vo(News news);

    Page<NewsVO> entity2PageVO(Page<News> newsPage);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

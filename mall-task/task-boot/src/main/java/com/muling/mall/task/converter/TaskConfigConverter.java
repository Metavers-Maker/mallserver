package com.muling.mall.task.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.task.pojo.entity.TaskConfig;
import com.muling.mall.task.pojo.form.admin.TaskConfigForm;
import com.muling.mall.task.pojo.vo.app.TaskConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskConfigConverter {

    TaskConfigConverter INSTANCE = Mappers.getMapper(TaskConfigConverter.class);

    TaskConfig form2po(TaskConfigForm form);

    void updatePo(TaskConfigForm form, @MappingTarget TaskConfig config);

    TaskConfigVO po2vo(TaskConfig config);

    Page<TaskConfigVO> entity2PageVO(Page<TaskConfig> configsPage);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

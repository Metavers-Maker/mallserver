package com.muling.mall.task.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.task.pojo.entity.TaskMember;
import com.muling.mall.task.pojo.vo.app.TaskMemberVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMemberConverter {

    TaskMemberConverter INSTANCE = Mappers.getMapper(TaskMemberConverter.class);

    TaskMemberVO po2vo(TaskMember taskMember);

    Page<TaskMemberVO> entity2PageVO(Page<TaskMember> page);

    List<TaskMemberVO> entity2ListVO(List<TaskMember> page);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

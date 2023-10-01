package com.muling.mall.task.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.task.pojo.entity.TaskMember;

public interface ITaskMemberService extends IService<TaskMember> {

    public boolean exist(Long memberId, Long taskId);

    public TaskMember get(Long memberId, Long taskId);

}

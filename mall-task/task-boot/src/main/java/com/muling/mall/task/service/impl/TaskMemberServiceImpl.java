package com.muling.mall.task.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.mall.task.mapper.TaskMemberMapper;
import com.muling.mall.task.pojo.entity.TaskMember;
import com.muling.mall.task.service.ITaskMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskMemberServiceImpl extends ServiceImpl<TaskMemberMapper, TaskMember> implements ITaskMemberService {

    private final RedissonClient redissonClient;

    private final TaskConfigServiceImpl taskConfigService;


    @Override
    public boolean exist(Long memberId, Long taskId) {
        return this.baseMapper.exists(Wrappers.<TaskMember>lambdaQuery()
                .eq(TaskMember::getMemberId, memberId)
                .eq(TaskMember::getTaskId, taskId));
    }

    /**
     * 获取任务
     */
    @Override
    public TaskMember get(Long memberId, Long taskId) {
        TaskMember taskMember = this.baseMapper.selectOne(Wrappers.<TaskMember>lambdaQuery()
                .eq(TaskMember::getMemberId, memberId)
                .eq(TaskMember::getTaskId, taskId));
        return taskMember;
    }

    //end class
}

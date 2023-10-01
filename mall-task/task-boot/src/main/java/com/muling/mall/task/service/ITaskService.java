package com.muling.mall.task.service;


import com.muling.mall.task.event.TaskCheckSuccessEvent;
import com.muling.mall.task.pojo.form.app.SubmitTaskItemForm;

public interface ITaskService {

    /**
     * 领取任务
     *
     * @param taskId
     * @return
     */
    public boolean draw(Long taskId);

    /**
     * 提交任务
     *
     * @param taskItemForm
     * @return
     */
    public boolean submit(SubmitTaskItemForm taskItemForm);


    /**
     * 审核
     *
     * @param taskMemberId
     * @param status
     * @return
     */
    public boolean check(Long taskMemberId, Integer status);


    /**
     * 审核成功
     *
     * @param event
     * @return
     */
    public boolean checkSuccess(TaskCheckSuccessEvent event);
}

package com.muling.mall.task.pojo.vo.app;


import lombok.Data;

@Data
public class TaskMemberVO {

    /**
     * 任务ID
     * */
    private Long id;

    /**
     * 任务名称
     * */
    private String name;

    /**
     * 任务次数
     * */
    private Integer times;

}

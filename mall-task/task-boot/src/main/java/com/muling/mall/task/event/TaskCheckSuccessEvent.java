package com.muling.mall.task.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskCheckSuccessEvent implements Serializable {

    private Long memberId;

    private Long taskId;

    private Long taskMemberId;


}

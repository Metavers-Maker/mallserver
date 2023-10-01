package com.muling.mall.ums.component.job;

import com.muling.mall.ums.service.IUmsMemberService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberJob {

    private final IUmsMemberService memberService;

    @XxlJob(value = "memberJobHandler")
    public ReturnT<String> memberJobHandler(String param) throws Exception {

        return ReturnT.SUCCESS;
    }
}

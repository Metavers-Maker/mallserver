package com.muling.mall.ums.job;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.ums.pojo.entity.UmsMemberInvite;
import com.muling.mall.ums.service.IUmsMemberInviteService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class ActiveValueXxlJob {
    private static Logger logger = LoggerFactory.getLogger(ActiveValueXxlJob.class);

    @Resource
    private IUmsMemberInviteService memberInviteService;

    @XxlJob(value = "activeValueHandler")
    public ReturnT<String> closeFarmBagHandler(String param) {

        XxlJobHelper.log("-------activeValueHandler start--------- ");
        try {

        } catch (Exception e) {
            logger.error("", e);
            throw e;
        } finally {
            XxlJobHelper.log("-------activeValueHandler end--------- ");
        }
        return ReturnT.SUCCESS;
    }

}

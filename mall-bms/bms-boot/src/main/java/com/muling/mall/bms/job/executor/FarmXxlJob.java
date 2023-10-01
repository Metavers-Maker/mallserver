package com.muling.mall.bms.job.executor;


import com.muling.mall.bms.service.IFarmService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class FarmXxlJob {
    private static Logger logger = LoggerFactory.getLogger(FarmXxlJob.class);

    @Resource
    private IFarmService farmService;

    @XxlJob(value = "farmingHandler")
    public ReturnT<String> farmingHandler(String param) {

        XxlJobHelper.log("-------farmingHandler start--------- ");
        try {
            farmService.settle();
        } catch (Exception e) {
            logger.error("", e);
            throw e;
        } finally {
            XxlJobHelper.log("-------farmingHandler end--------- ");
        }
        return ReturnT.SUCCESS;
    }

}

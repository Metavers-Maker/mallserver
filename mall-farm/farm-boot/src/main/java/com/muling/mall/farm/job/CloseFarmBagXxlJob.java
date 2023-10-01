package com.muling.mall.farm.job;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.farm.pojo.entity.FarmMember;
import com.muling.mall.farm.pojo.entity.FarmMemberItem;
import com.muling.mall.farm.pojo.vo.app.FarmMemberVO;
import com.muling.mall.farm.service.IFarmService;
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
public class CloseFarmBagXxlJob {
    private static Logger logger = LoggerFactory.getLogger(CloseFarmBagXxlJob.class);

    @Resource
    private IFarmService farmService;

    @XxlJob(value = "closeFarmBagHandler")
    public ReturnT<String> closeFarmBagHandler(String param) {

        XxlJobHelper.log("-------closeFarmBagHandler start--------- ");
        try {
            Integer pageNum = 1;
            Integer pageSize = 100;
            Page<FarmMember> page = farmService.pageFarm(pageNum, pageSize);
            if (page.getRecords().size() > 0) {
                farmService.resetFarms(page.getRecords());
                while (page.hasNext()) {
                    pageNum = pageNum + 1;
                    page = farmService.pageFarm(pageNum, pageSize);
                    farmService.resetFarms(page.getRecords());
                }
            }
        } catch (Exception e) {
            logger.error("", e);
            throw e;
        } finally {
            XxlJobHelper.log("-------closeFarmBagHandler end--------- ");
        }
        return ReturnT.SUCCESS;
    }

}

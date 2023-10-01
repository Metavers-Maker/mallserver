package com.muling.mall.bms;

import com.muling.mall.bms.service.IBmsBsnService;
import com.muling.mall.bms.service.IBmsBsnSwapService;
import com.muling.mall.bms.service.ICoinRewardConfigService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * Created by Given on 2021/12/6
 */

@SpringBootTest
@Slf4j
public class BmsServiceTest {

    @Resource
    private IBmsBsnService bsnService;

    @Resource
    private ICoinRewardConfigService rewardConfigService;

    @Resource
    private IBmsBsnSwapService bmsBsnSwapService;

    @Test
    void mintItem() {
        //
        boolean ret = bsnService.mintById(38l);
        int a = 0;
    }

    @Test
    void queryItem() {
        //
        boolean ret = bsnService.mintQueryById(38l);
        int a = 0;
    }

    @Test
    void mintItemBySpu() {
        //
        bsnService.mintBySpu(20230604600000001l);
//        rewardConfigService.calculate();
    }

    @Test
    void stickReward() {
        //
        rewardConfigService.calculate();
    }

    @Test
    void transItemAuto() {
        //
        bsnService.execTransSchedule();
    }

    public static void main(String[] args) {
        //
        int a = 0;
    }

}

package com.muling.mall.wms;

import com.muling.mall.wms.enums.WalletOpTypeEnum;
import com.muling.mall.wms.pojo.dto.WalletDTO;
import com.muling.mall.wms.service.IWmsWalletService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * Created by Given on 2021/12/6
 */
@SpringBootTest
@Slf4j
public class WmsWalletServiceTest {

    @Resource
    private IWmsWalletService walletService;

    @Test
    void authTest() {
        WalletDTO walletForm = new WalletDTO();

        walletForm.setMemberId(2L);
        walletForm.setBalance(new BigDecimal("1").negate());
        walletForm.setCoinType(3);
        walletForm.setOpType(WalletOpTypeEnum.FARM_BAG_CLOSE_CONSUME.getValue());
        walletForm.setRemark(WalletOpTypeEnum.FARM_BAG_CLOSE_CONSUME.getLabel());
        walletService.updateBalance(walletForm);
    }


}

package com.muling.mall.wms.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.exception.BizException;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.wms.common.constant.WmsConstants;
import com.muling.mall.wms.enums.WalletOpTypeEnum;
import com.muling.mall.wms.pojo.dto.WalletDTO;
import com.muling.mall.wms.pojo.entity.WmsSwapConfig;
import com.muling.mall.wms.pojo.form.app.NewSwapForm;
import com.muling.mall.wms.pojo.query.app.SwapConfigPageQuery;
import com.muling.mall.wms.pojo.vo.SwapConfigVO;
import com.muling.mall.wms.service.IWmsSwapConfigService;
import com.muling.mall.wms.service.IWmsSwapService;
import com.muling.mall.wms.service.IWmsWalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WmsSwapServiceImpl implements IWmsSwapService {

    private final IWmsSwapConfigService swapConfigService;
    private final RedissonClient redissonClient;
    private final IWmsWalletService walletService;


    @Override
    public IPage<SwapConfigVO> page(SwapConfigPageQuery queryParams) {

        IPage<SwapConfigVO> page = swapConfigService.page(queryParams);
        return page;
    }

    @Override
    @Transactional
    public boolean swap(NewSwapForm swapForm) {
        //记得加锁
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_LOCK_UPDATE_PREFIX + memberId);

        try {
            lock.lock();

            Long swapId = swapForm.getSwapId();
            BigDecimal sourceCoinValue = swapForm.getSourceCoinValue();
            WmsSwapConfig config = swapConfigService.getById(swapId);
            Assert.isTrue(config != null, "配置未找到");

            if (sourceCoinValue.compareTo(config.getSourceMinValue()) < 0 || sourceCoinValue.compareTo(config.getSourceMaxValue()) > 0) {
                throw new BizException("兑换数量不符合要求");
            }

            BigDecimal fee = BigDecimal.ZERO;
            if (config.getFee() != null && config.getFee().compareTo(BigDecimal.ZERO) > 0) {
                if (config.getFeeType() == 0) {//固定
                    fee = config.getFee();
                } else if (config.getFeeType() == 1) {//比例
                    fee = config.getFee().multiply(sourceCoinValue);
                }
            }

            BigDecimal totalFee = fee.compareTo(config.getMinFee()) > 0 ? fee : config.getMinFee();

            //目标数量
            BigDecimal targetCoinValue = sourceCoinValue.multiply(config.getRatio());


            List<WalletDTO> wallets = new ArrayList<>();
            WalletDTO source = new WalletDTO()
                    .setMemberId(memberId)
                    .setBalance(sourceCoinValue.negate())
                    .setFee(totalFee.negate())
                    .setCoinType(config.getSourceCoinType())
                    .setOpType(WalletOpTypeEnum.SWAP_CONSUME.getValue())
                    .setRemark(WalletOpTypeEnum.SWAP_CONSUME.getLabel());
            WalletDTO target = new WalletDTO()
                    .setMemberId(memberId)
                    .setBalance(targetCoinValue)
                    .setCoinType(config.getSourceCoinType())
                    .setOpType(WalletOpTypeEnum.SWAP_RECEIVE.getValue())
                    .setRemark(WalletOpTypeEnum.SWAP_RECEIVE.getLabel());

            wallets.add(source);
            wallets.add(target);

            walletService.updateBalances(wallets);

        } catch (Exception e) {
            log.error("", e);
            throw e;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return true;
    }


}

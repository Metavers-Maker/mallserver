package com.muling.mall.wms.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.exception.BizException;
import com.muling.common.result.Result;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.pojo.dto.MemberAuthDTO;
import com.muling.mall.wms.common.constant.WmsConstants;
import com.muling.mall.wms.converter.TransferConfigConverter;
import com.muling.mall.wms.enums.WalletOpTypeEnum;
import com.muling.mall.wms.pojo.dto.WalletDTO;
import com.muling.mall.wms.pojo.entity.WmsTransferConfig;
import com.muling.mall.wms.pojo.entity.WmsTransferLog;
import com.muling.mall.wms.pojo.form.app.TransferForm;
import com.muling.mall.wms.pojo.query.app.TransferPageQuery;
import com.muling.mall.wms.pojo.vo.TransferVO;
import com.muling.mall.wms.service.IWmsTransferConfigService;
import com.muling.mall.wms.service.IWmsTransferLogService;
import com.muling.mall.wms.service.IWmsTransferService;
import com.muling.mall.wms.service.IWmsWalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WmsTransferServiceImpl implements IWmsTransferService {

    private final IWmsTransferConfigService transferConfigService;
    private final MemberFeignClient memberFeignClient;
    private final StringRedisTemplate redisTemplate;
    private final RedissonClient redissonClient;
    private final IWmsWalletService walletService;
    private final IWmsTransferLogService transferLogService;


    @Override
    public IPage<TransferVO> page(TransferPageQuery queryParams) {

        LambdaQueryWrapper<WmsTransferConfig> wrapper = Wrappers.<WmsTransferConfig>lambdaQuery()
                .eq(queryParams.getCoinType() != null, WmsTransferConfig::getCoinType, queryParams.getCoinType())
                .orderByDesc(WmsTransferConfig::getUpdated);
        ;
        IPage page = transferConfigService.page(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);

        List<TransferVO> list = TransferConfigConverter.INSTANCE.po2voList(page.getRecords());

        return page.setRecords(list);
    }

    @Override
    @Transactional
    public boolean transfer(TransferForm transferForm) {
        //记得加锁
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_LOCK_UPDATE_PREFIX + memberId);

        try {
            lock.lock();

            Integer coinType = transferForm.getCoinType();
            String uid = transferForm.getToUid();
            BigDecimal typeValue = transferForm.getTypeValue();

            Result<MemberAuthDTO> toMember = memberFeignClient.loadUserByUid(uid);
            Assert.isTrue(Result.isSuccess(toMember), "没有找到该用户");
            Long toMemberId = toMember.getData().getMemberId();

            WmsTransferConfig config = transferConfigService.getByCoinType(coinType);
            Assert.isTrue(config != null, "配置未找到");

            Assert.isTrue(toMember.getData().getMemberId().longValue() != memberId.longValue(), "不能给自己转移");

            if (typeValue.compareTo(config.getMinValue()) < 0 || typeValue.compareTo(config.getMaxValue()) > 0) {
                throw new BizException("转移金额不符合要求");
            }

            List<WalletDTO> wallets = new ArrayList<>();

            BigDecimal fee = BigDecimal.ZERO;
            if (config.getFee() != null && config.getFee().compareTo(BigDecimal.ZERO) > 0) {
                if (config.getFeeType() == 0) {
                    fee = config.getFee();
                } else if (config.getFeeType() == 1) {
                    fee = config.getFee().multiply(typeValue);
                }
            }

            BigDecimal totalFee = fee.compareTo(config.getMinFee()) > 0 ? fee : config.getMinFee();

            WalletDTO source = new WalletDTO()
                    .setMemberId(memberId)
                    .setBalance(typeValue.negate())
                    .setFee(totalFee.negate())
                    .setCoinType(coinType)
                    .setOpType(WalletOpTypeEnum.TRANSFER.getValue())
                    .setRemark("转赠");
            WalletDTO target = new WalletDTO()
                    .setMemberId(toMemberId)
                    .setBalance(typeValue)
                    .setCoinType(coinType)
                    .setOpType(WalletOpTypeEnum.TRANSFER_RECEIVE.getValue())
                    .setRemark("转赠接收");

            wallets.add(source);
            wallets.add(target);

            walletService.updateBalances(wallets);

            WmsTransferLog transferLog = new WmsTransferLog()
                    .setSourceId(memberId)
                    .setTargetId(toMemberId)
                    .setTargetUid(uid)
                    .setCoinType(coinType)
                    .setBalance(typeValue)
                    .setFee(totalFee)
                    .setRemark("转赠");

            WmsTransferLog transferReceiveLog = new WmsTransferLog()
                    .setSourceId(toMemberId)
                    .setTargetId(memberId)
                    .setTargetUid(uid)
                    .setCoinType(coinType)
                    .setBalance(typeValue)
                    .setRemark("转赠接收");

            List<WmsTransferLog> logs = new ArrayList<>();
            logs.add(transferLog);
            logs.add(transferReceiveLog);
            transferLogService.saveBatch(logs);

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

    public static void main(String[] args) {
        WmsTransferConfig config = new WmsTransferConfig();
        config.setFee(new BigDecimal("0.03"));
        config.setCoinType(1);
        config.setFeeType(1);
        config.setMinFee(BigDecimal.ONE);

        BigDecimal typeValue = new BigDecimal("100");

        BigDecimal fee = BigDecimal.ZERO;
        if (config.getFee() != null && config.getFee().compareTo(BigDecimal.ZERO) > 0) {
            if (config.getFeeType() == 0) {
                fee = config.getFee();
            } else if (config.getFeeType() == 1) {
                fee = config.getFee().multiply(typeValue);
            }
        }
        System.out.println(fee);
        BigDecimal totalFee = fee.compareTo(config.getMinFee()) > 0 ? fee : config.getMinFee();

        System.out.println(totalFee);
    }
}

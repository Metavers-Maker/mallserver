package com.muling.mall.wms.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.base.IBaseEnum;
import com.muling.common.exception.BizException;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import com.muling.mall.wms.common.constant.WmsConstants;
import com.muling.mall.wms.common.enums.CoinStatusEnum;
import com.muling.mall.wms.converter.WalletConverter;
import com.muling.mall.wms.enums.WalletOpTypeEnum;
import com.muling.mall.wms.internal.InternalWalletDTO;
import com.muling.mall.wms.mapper.WmsWalletMapper;
import com.muling.mall.wms.pojo.dto.WalletDTO;
import com.muling.mall.wms.pojo.entity.WmsMarketLog;
import com.muling.mall.wms.pojo.entity.WmsWallet;
import com.muling.mall.wms.pojo.entity.WmsWalletLog;
import com.muling.mall.wms.pojo.form.admin.WalletForm;
import com.muling.mall.wms.pojo.form.app.SwapForm;
import com.muling.mall.wms.pojo.query.app.WalletPageQuery;
import com.muling.mall.wms.pojo.vo.WalletVO;
import com.muling.mall.wms.service.IWmsWalletLogService;
import com.muling.mall.wms.service.IWmsWalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WmsWalletServiceImpl extends ServiceImpl<WmsWalletMapper, WmsWallet> implements IWmsWalletService {

    private final IWmsWalletLogService walletLogService;

    private final RedissonClient redissonClient;

    //
    private final MemberFeignClient memberFeignClient;

//    private final IWmsTransferLogService transferLogService;

    public IPage<WalletVO> page(WalletPageQuery queryParams) {

        Long memberId = MemberUtils.getMemberId();

        LambdaQueryWrapper<WmsWallet> wrapper = Wrappers.<WmsWallet>lambdaQuery()
                .eq(WmsWallet::getMemberId, memberId)
                .eq(queryParams.getCoinType() != null, WmsWallet::getCoinType, queryParams.getCoinType())
                .orderByDesc(WmsWallet::getCreated);
        ;
        Page<WmsWallet> page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);

        Page<WalletVO> voPage = WalletConverter.INSTANCE.entity2PageVO(page);
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(WalletForm walletForm) {
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_LOCK_UPDATE_PREFIX + walletForm.getMemberId());
        boolean result = false;
        try {
            lock.lock();
            //
            Result<MemberDTO> memberDTOResult = memberFeignClient.getMemberById(walletForm.getMemberId());
            Assert.isTrue(memberDTOResult.getData().getStatus().intValue() == 1, "账号封禁");
            //
            WalletDTO walletDTO = WalletConverter.INSTANCE.form2dto(walletForm);
            walletDTO.setOpType(WalletOpTypeEnum.ADMIN.getValue());
            InternalWalletDTO internalWalletDTO = internalUpdateBalance(walletDTO);
            if (internalWalletDTO.isCanUpdate()) {
                result = this.saveOrUpdate(internalWalletDTO.getWallet());
                if (!result) {
                    throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
                }
                result = walletLogService.save(internalWalletDTO.getWalletLog());
            }
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateBalance(WalletDTO walletDTO) {

        RLock lock = redissonClient.getLock(WmsConstants.WALLET_LOCK_UPDATE_PREFIX + walletDTO.getMemberId());
        boolean result = false;
        try {
            lock.lock();
            //
            Result<MemberDTO> memberDTOResult = memberFeignClient.getMemberById(walletDTO.getMemberId());
            Assert.isTrue(memberDTOResult.getData().getStatus().intValue() == 1, "账号封禁");
            //
            InternalWalletDTO internalWalletDTO = internalUpdateBalance(walletDTO);
            if (internalWalletDTO.isCanUpdate()) {
                result = this.saveOrUpdate(internalWalletDTO.getWallet());
                if (!result) {
                    throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
                }
                walletLogService.save(internalWalletDTO.getWalletLog());
            }
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return result;
    }


    private InternalWalletDTO internalUpdateBalance(WalletDTO walletDTO) {
//        boolean result = false;
        WmsWallet wallet = this.baseMapper.selectByMemberIdAndCoinType(walletDTO.getMemberId(), walletDTO.getCoinType());
        BigDecimal oldBalance = BigDecimal.ZERO;
        if (wallet != null) {
            CoinStatusEnum status = wallet.getStatus();
            if (status == CoinStatusEnum.DISABLED) {
                InternalWalletDTO internalWalletDTO = new InternalWalletDTO()
                        .setWallet(null)
                        .setWalletLog(null)
                        .setCanUpdate(false);
                return internalWalletDTO;
            }
            //新值
            BigDecimal newBalance = BigDecimal.ZERO;
            //原值
            oldBalance = wallet.getBalance();
            //传入值
            BigDecimal outBalance = walletDTO.getBalance().add(walletDTO.getFee());

            //传入值为正，即加相，传入值为负，即相减。判断原值与传入值操作后是否为负，为负则新值为0
            newBalance = oldBalance.add(outBalance);

            //钱包不允许负数存在
            int newBalanceCompareTo = newBalance.compareTo(BigDecimal.ZERO);
            if (newBalanceCompareTo < 0) {
                throw new BizException(ResultCode.WALLET_BALANCE_NOT_ENOUGH);
            }
            wallet.setBalance(newBalance);
        } else {
            if (walletDTO.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                throw new BizException(ResultCode.WALLET_BALANCE_NOT_ENOUGH);
            }
            wallet = WalletConverter.INSTANCE.dto2po(walletDTO);
        }

        WmsWalletLog walletLog = new WmsWalletLog()
                .setBalance(wallet.getBalance())
                .setOldBalance(oldBalance)
                .setInBalance(walletDTO.getBalance().add(walletDTO.getFee()))
                .setCoinType(wallet.getCoinType())
                .setFee(walletDTO.getFee())
                .setMemberId(wallet.getMemberId())
                .setOpType(IBaseEnum.getEnumByValue(walletDTO.getOpType(), WalletOpTypeEnum.class))
                .setRemark(walletDTO.getRemark());

        InternalWalletDTO internalWalletDTO = new InternalWalletDTO()
                .setWallet(wallet)
                .setWalletLog(walletLog)
                .setCanUpdate(true);
//        return walletLogService.save(walletLog);
        return internalWalletDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBalances(List<WalletDTO> list) {
        if (list.isEmpty()) {
            log.info("更新钱包列表数据为空:");
            return false;
        }
        WalletDTO walletDTO = list.get(0);
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_LOCK_UPDATE_PREFIX + walletDTO.getMemberId());
        boolean result = false;
        try {
            lock.lock();
            List<WmsWalletLog> logs = Lists.newArrayList();
            for (WalletDTO dto : list) {
                InternalWalletDTO internalWalletDTO = internalUpdateBalance(dto);
                if (internalWalletDTO.isCanUpdate()) {
                    //这里以后可以加乐观锁，因为批量时现有分布式锁可能不管用
                    result = this.saveOrUpdate(internalWalletDTO.getWallet());
                    if (!result) {
                        throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
                    }
                    //批量更新日志
                    logs.add(internalWalletDTO.getWalletLog());
                }
            }
            if (!logs.isEmpty()) {
                walletLogService.saveBatch(logs);
            }
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return result;
    }

    @Override
    public WmsWallet getCoinByMemberIdAndCoinType(Long memberId, Integer coinType) {
        WmsWallet wallet = this.baseMapper.selectByMemberIdAndCoinType(memberId, coinType);
        return wallet;
    }

    @Override
    @Transactional
    public WalletVO swap(SwapForm swapForm) {
        //记得加锁
        Long memberId = MemberUtils.getMemberId();
        WalletVO walletVO = null;
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_LOCK_UPDATE_PREFIX + memberId);
        try {
            lock.lock();
            //5-任务积分
            Integer srcCoinType = swapForm.getSrcCoinType();
            Assert.isTrue(srcCoinType == 5, "源钱包类型不正确");
            WmsWallet srcWmsWallet = this.getCoinByMemberIdAndCoinType(memberId, srcCoinType);
            Assert.isTrue(srcWmsWallet != null, "源钱包不存在");
            Assert.isTrue(srcWmsWallet.getStatus() == CoinStatusEnum.ENABLED, "源钱包禁用");
            Assert.isTrue(srcWmsWallet.getBalance().compareTo(swapForm.getSrcCoinValue()) >= 0, "源钱包余额不足");
            //4-RMB
            Integer dstCoinType = swapForm.getDstCoinType();
            Assert.isTrue(dstCoinType == 4, "目标钱包类型不正确");
            //获取目标钱包
            WmsWallet dstWmsWallet = this.getCoinByMemberIdAndCoinType(memberId, dstCoinType);
            if (dstWmsWallet == null) {
                //创建目标钱包
                WalletDTO dstWalletDto = new WalletDTO();
                dstWalletDto.setMemberId(memberId);
                dstWalletDto.setCoinType(dstCoinType);
                dstWalletDto.setBalance(BigDecimal.valueOf(0.0));
                dstWalletDto.setFee(BigDecimal.valueOf(0.0));
                dstWalletDto.setOpType(WalletOpTypeEnum.EXCHANGE_RECEIVE.getValue());
                dstWalletDto.setRemark("创建钱包");
                dstWmsWallet = WalletConverter.INSTANCE.dto2po(dstWalletDto);
                this.save(dstWmsWallet);
            }
            //
            BigDecimal srcOldBalance = srcWmsWallet.getBalance();
            BigDecimal srcBalance = srcOldBalance.add(swapForm.getSrcCoinValue().negate());
            //
            BigDecimal dstOldBalance = dstWmsWallet.getBalance();
            BigDecimal dstCoinValue = swapForm.getSrcCoinValue().multiply(BigDecimal.valueOf(0.001));
            BigDecimal dstBalance = dstOldBalance.add(dstCoinValue);
            //1000:1
            List<WalletDTO> wallets = new ArrayList<>();
            WalletDTO source = new WalletDTO()
                    .setMemberId(memberId)
                    .setBalance(swapForm.getSrcCoinValue().negate())
                    .setFee(BigDecimal.valueOf(0.0))
                    .setCoinType(srcCoinType)
                    .setOpType(WalletOpTypeEnum.EXCHANGE_CONSUME.getValue())
                    .setRemark("币币兑换消耗");
            WalletDTO target = new WalletDTO()
                    .setMemberId(memberId)
                    .setBalance(dstCoinValue)
                    .setCoinType(dstCoinType)
                    .setOpType(WalletOpTypeEnum.EXCHANGE_RECEIVE.getValue())
                    .setRemark("币币兑换接收");
            wallets.add(source);
            wallets.add(target);
            //更新钱包余额
            this.updateBalances(wallets);
            //返回Dto2Vo
            walletVO = new WalletVO();
            walletVO.setStatus(1);
            walletVO.setMemberId(memberId);
            walletVO.setBalance(dstCoinValue);
            walletVO.setCoinType(dstCoinType);
            //记log
            WmsWalletLog swapLog = new WmsWalletLog()
                    .setMemberId(memberId)
                    .setOpType(WalletOpTypeEnum.EXCHANGE_CONSUME)
                    .setCoinType(srcCoinType)
                    .setOldBalance(srcOldBalance)
                    .setInBalance(swapForm.getSrcCoinValue())
                    .setBalance(srcBalance)
                    .setFee(BigDecimal.valueOf(0.0))
                    .setRemark("币币兑换消耗");
            //
            WmsWalletLog swapReceiveLog = new WmsWalletLog()
                    .setMemberId(memberId)
                    .setOpType(WalletOpTypeEnum.EXCHANGE_RECEIVE)
                    .setCoinType(dstCoinType)
                    .setOldBalance(dstOldBalance)
                    .setInBalance(dstCoinValue)
                    .setBalance(dstBalance)
                    .setFee(BigDecimal.valueOf(0.0))
                    .setRemark("币币兑换接收");
            //
            List<WmsWalletLog> logs = new ArrayList<>();
            logs.add(swapLog);
            logs.add(swapReceiveLog);
            walletLogService.saveBatch(logs);
            //
        } catch (Exception e) {
            log.error("", e);
            throw e;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return walletVO;
    }

    public BigDecimal total(Integer coinType) {
        BigDecimal totalBalance = new BigDecimal(0.0);
        LambdaQueryWrapper<WmsWallet> wrapper = Wrappers.<WmsWallet>lambdaQuery()
                .eq(WmsWallet::getCoinType, coinType)
                .eq(WmsWallet::getStatus, CoinStatusEnum.ENABLED)
                .orderByDesc(WmsWallet::getCreated);
        Integer pageNum = 1;
        Integer pageSize = 1000;
        Page<WmsWallet> page = this.page(new Page<>(pageNum, pageSize),wrapper);
        if (page.getRecords().size() > 0) {
            for (WmsWallet wmsWallet : page.getRecords()) {
                totalBalance = totalBalance.add(wmsWallet.getBalance());
            }
            while (page.hasNext()) {
                pageNum = pageNum + 1;
                page = this.page(new Page<>(pageNum, pageSize),wrapper);
                for (WmsWallet wmsWallet : page.getRecords()) {
                    totalBalance = totalBalance.add(wmsWallet.getBalance());
                }
            }
        }
        return totalBalance;
    }

    @Override
    public Boolean feng(Long memberId, Integer status) {
        boolean f = false;
        if (status == 0) {
            LambdaUpdateWrapper<WmsWallet> wrapper = new LambdaUpdateWrapper<WmsWallet>()
                    .eq(WmsWallet::getMemberId, memberId)
                    .set(WmsWallet::getStatus, CoinStatusEnum.DISABLED);
            f = this.update(wrapper);
        } else {
            LambdaUpdateWrapper<WmsWallet> wrapper = new LambdaUpdateWrapper<WmsWallet>()
                    .eq(WmsWallet::getMemberId, memberId)
                    .set(WmsWallet::getStatus, CoinStatusEnum.ENABLED);
            f = this.update(wrapper);
        }
        return f;
    }

    public static void main(String[] args) {
        int i = Math.addExact(6, -1);
        System.out.println(i);
    }
}

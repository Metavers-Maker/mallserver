package com.muling.mall.wms.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.exception.BizException;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import com.muling.mall.wms.common.constant.WmsConstants;
import com.muling.mall.wms.converter.WalletConverter;
import com.muling.mall.wms.converter.WithdrawConverter;
import com.muling.mall.wms.enums.WalletOpTypeEnum;
import com.muling.mall.wms.internal.InternalWalletDTO;
import com.muling.mall.wms.mapper.WmsWithdrawMapper;
import com.muling.mall.wms.pojo.dto.WalletDTO;
import com.muling.mall.wms.pojo.entity.WmsWallet;
import com.muling.mall.wms.pojo.entity.WmsWithdraw;
import com.muling.mall.wms.pojo.query.app.WithdrawPageQuery;
import com.muling.mall.wms.pojo.vo.WithdrawVO;
import com.muling.mall.wms.service.IWmsWithdrawService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import software.amazon.ion.Decimal;

@Service
@RequiredArgsConstructor
public class WmsWithdrawServiceImpl extends ServiceImpl<WmsWithdrawMapper, WmsWithdraw> implements IWmsWithdrawService {

    //
    private final RedissonClient redissonClient;

    private final MemberFeignClient memberFeignClient;

    private final WmsWalletServiceImpl wmsWalletService;


    public IPage<WithdrawVO> page(WithdrawPageQuery queryParams) {

        Long memberId = MemberUtils.getMemberId();
        LambdaQueryWrapper<WmsWithdraw> wrapper = Wrappers.<WmsWithdraw>lambdaQuery()
                .eq(WmsWithdraw::getMemberId, memberId)
//                .eq(queryParams.getCoinType() != null, WmsWithdraw::getCoinType, queryParams.getCoinType())
                .orderByDesc(WmsWithdraw::getCreated);
        ;
        Page<WmsWithdraw> page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);

        Page<WithdrawVO> voPage = WithdrawConverter.INSTANCE.entity2PageVO(page);
        return voPage;
    }

    public WithdrawVO withdraw(Decimal balance,Integer coinType) {
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_WITHDRAW_PREFIX + memberId);
        boolean result = false;
        try {
            lock.lock();
            //
            Assert.isTrue(coinType.intValue() == 4 , "非指定积分");
            //
            Result<MemberDTO> memberDTOResult = memberFeignClient.getMemberById(memberId);
            Assert.isTrue(memberDTOResult.getData().getStatus().intValue() == 1, "账号封禁");
            //检测用户余额 5
            WmsWallet wmsWallet = wmsWalletService.getCoinByMemberIdAndCoinType(memberId,coinType);
            Assert.isTrue(wmsWallet != null, "钱包不存在");
            Assert.isTrue(wmsWallet.getBalance().compareTo(balance)>0, "余额不足");
            //生成 withdraw bill
            WmsWithdraw wmsWithdraw = new WmsWithdraw();
            wmsWithdraw.setMemberId(memberId);
            wmsWithdraw.setBalance(balance);
            wmsWithdraw.setReason("");
            wmsWithdraw.setStatus(0);
            boolean f = this.save(wmsWithdraw);
            if (f) {
                //冻结积分
                WalletDTO walletDTO = new WalletDTO();
                walletDTO.setMemberId(memberId);
                walletDTO.setBalance(wmsWithdraw.getBalance().negate());
                walletDTO.setCoinType(4);
                walletDTO.setOpType(WalletOpTypeEnum.WITHDRAW_FREEZE.getValue());
                walletDTO.setRemark(WalletOpTypeEnum.WITHDRAW_FREEZE.getLabel());
                wmsWalletService.updateBalance(walletDTO);
            }
            return null;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
    //
    public boolean cancle(Integer id) {
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_WITHDRAW_OP_PREFIX + id);
        boolean result = false;
        try {
            lock.lock();
            WmsWithdraw wmsWithdraw = this.getById(id);
            Assert.isTrue(wmsWithdraw.getMemberId().compareTo(memberId) == 0,"非自己的订单");
            wmsWithdraw.setStatus(2);
            wmsWithdraw.setReason("用户主动取消");
            result = this.updateById(wmsWithdraw);
            if (result) {
                //记录日志，解冻积分
                WalletDTO walletDTO = new WalletDTO();
                walletDTO.setMemberId(memberId);
                walletDTO.setBalance(wmsWithdraw.getBalance());
                walletDTO.setCoinType(4);
                walletDTO.setOpType(WalletOpTypeEnum.WITHDRAW_UNFREEZE.getValue());
                walletDTO.setRemark(WalletOpTypeEnum.WITHDRAW_UNFREEZE.getLabel());
                wmsWalletService.updateBalance(walletDTO);
            }
            return result;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * admin 通过操作
     * */
    public boolean pass(Integer id) {
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_WITHDRAW_OP_PREFIX + id);
        boolean result = false;
        try {
            lock.lock();
            WmsWithdraw wmsWithdraw = this.getById(id);
            wmsWithdraw.setStatus(2);
            result = this.updateById(wmsWithdraw);
            if (result) {
                //记录日志
            }
            return result;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * admin 拒绝操作
     * */
    public boolean reject(Integer id,String reason)  {
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_WITHDRAW_OP_PREFIX + id);
        boolean result = false;
        try {
            lock.lock();
            WmsWithdraw wmsWithdraw = this.getById(id);
            wmsWithdraw.setStatus(2);
            wmsWithdraw.setReason(reason);
            result = this.updateById(wmsWithdraw);
            if (result) {
                //记录日志，解冻积分
                WalletDTO walletDTO = new WalletDTO();
                walletDTO.setMemberId(wmsWithdraw.getMemberId());
                walletDTO.setBalance(wmsWithdraw.getBalance());
                walletDTO.setCoinType(4);
                walletDTO.setOpType(WalletOpTypeEnum.WITHDRAW_REJECT.getValue());
                walletDTO.setRemark(WalletOpTypeEnum.WITHDRAW_REJECT.getLabel());
                wmsWalletService.updateBalance(walletDTO);
            }
            return result;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    //
}

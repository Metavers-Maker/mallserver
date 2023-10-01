package com.muling.mall.ums.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.cert.service.HttpApiClientBSN;
import com.muling.common.enums.VCodeTypeEnum;
import com.muling.common.exception.BizException;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.util.MD5Util;
import com.muling.common.util.VCodeUtils;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.ums.constant.UmsConstants;
import com.muling.mall.ums.converter.AccountChainConverter;
import com.muling.mall.ums.enums.AccountChainEnum;
import com.muling.mall.ums.enums.MemberAuthStatusEnum;
import com.muling.mall.ums.mapper.UmsAccountChainMapper;
import com.muling.mall.ums.pojo.dto.MemberAccountChainDTO;
import com.muling.mall.ums.pojo.dto.MemberSandDTO;
import com.muling.mall.ums.pojo.entity.UmsAccountChain;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.pojo.form.AddressChainForm;
import com.muling.mall.ums.pojo.form.AddressChainUnbindForm;
import com.muling.mall.ums.service.IUmsAccountChainService;
import com.muling.mall.ums.service.IUmsMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

import static com.muling.common.constant.GlobalConstants.STATUS_YES;

/**
 * 会员链上地址业务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UmsAccountChainServiceImpl extends ServiceImpl<UmsAccountChainMapper, UmsAccountChain> implements IUmsAccountChainService {

    private final RedissonClient redissonClient;

    private final HttpApiClientBSN httpApiClientBSN;

    private final IUmsMemberService memberService;

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 获取当前登录会员的地址列表
     *
     * @return
     */
    @Override
    public List<MemberAccountChainDTO> listAccount(Integer chainType) {
        Long memberId = MemberUtils.getMemberId();
        List<UmsAccountChain> umsAccountChainList = this.list(new LambdaQueryWrapper<UmsAccountChain>()
                .eq(UmsAccountChain::getMemberId, memberId)
                .eq(chainType != null, UmsAccountChain::getChainType, chainType)
                .orderByDesc(UmsAccountChain::getChainType)
                .orderByDesc(UmsAccountChain::getCreated)
        );
        List<MemberAccountChainDTO> memberAddressList = AccountChainConverter.INSTANCE.pos2dtos(umsAccountChainList);
        return memberAddressList;
    }

    /**
     * 生成BSN地址
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> genBsnAccount() {
        Long memberId = MemberUtils.getMemberId();
        return genBsnAccountByMemberId(memberId);
    }

    /**
     * 生成BSN地址
     *
     * @return
     */
    @Override
    public Result<String> genBsnAccountByMemberId(Long memberId) {
        String account = null;
        String name = null;
        boolean saveFlag = false;
        RLock lock = redissonClient.getLock(UmsConstants.USER_LOCK_CHAIN_ACCOUNT_PREFIX + memberId);
        try {
            lock.lock();
            UmsMember umsMember = memberService.getById(memberId);
            List<UmsAccountChain> umsAccountChainList = this.list(new LambdaQueryWrapper<UmsAccountChain>()
                    .eq(UmsAccountChain::getMemberId, memberId)
                    .eq(UmsAccountChain::getChainType, AccountChainEnum.ACCOUNT_BSN)
            );
            if (umsAccountChainList.isEmpty()) {
                //生成OpID
                JSONObject tjson = new JSONObject();
                tjson.set("memberId", memberId);
                tjson.set("tim", System.currentTimeMillis());
                JSONObject ret = httpApiClientBSN.createAccount(umsMember.getUid(), tjson.toString());
                if (ret.get("code").equals(200)) {
                    JSONObject data = (JSONObject) ret.get("data");
                    if (data != null) {
                        account = data.get("account").toString();
                        name = data.get("name").toString();
                        //插入数据
                        UmsAccountChain accountChain = new UmsAccountChain();
                        accountChain.setAddress(account);
                        accountChain.setChainType(AccountChainEnum.ACCOUNT_BSN.getValue());
                        accountChain.setMemberId(memberId);
                        accountChain.setStatus(0);
                        saveFlag = this.save(accountChain);
                        if (saveFlag) {
                            umsMember.setChainAddress(account);
                            memberService.updateById(umsMember);
                        }
                    }
                } else {

                }
            }
        } catch (Exception e) {
            //
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        if (saveFlag && account != null) {
            return Result.success(account);
        }
        return Result.failed("no generate bsn account!");
    }

    /**
     * SyncBSN地址
     */
    @Override
    public boolean syncBsnAccount(Long memberId) {
        UmsAccountChain accountChain = this.baseMapper.selectOne(new LambdaQueryWrapper<UmsAccountChain>()
                .eq(UmsAccountChain::getMemberId, memberId)
                .eq(UmsAccountChain::getChainType, 0));
        Assert.isTrue(accountChain != null, "用户BSN账号不存在");
        //
        boolean reUpdate = memberService.update(new LambdaUpdateWrapper<UmsMember>()
                .eq(UmsMember::getId, memberId)
                .set(UmsMember::getChainAddress, accountChain.getAddress()));
        return reUpdate;
    }

    /**
     * 绑定三方账户
     *
     * @param chainForm
     * @return
     */
    @Override
    public boolean bindAccount(AddressChainForm chainForm, Long memberId) {
        boolean saveFlag = false;
        RLock lock = redissonClient.getLock(UmsConstants.USER_LOCK_BIND_CHAIN_ACCOUNT_PREFIX + memberId);
        try {
            lock.lock();
            String existCode = VCodeUtils.getCode(stringRedisTemplate, VCodeTypeEnum.BIND_THIRD_PLATFORM, chainForm.getMobile());
            log.info("绑定账户 验证码 {}", existCode);
            boolean b = VCodeUtils.checkVCode(stringRedisTemplate, VCodeTypeEnum.BIND_THIRD_PLATFORM, chainForm.getMobile(), chainForm.getCode());
            if (!b) {
                throw new BizException(ResultCode.VERIFY_CODE_ERROR);
            }
            List<UmsAccountChain> umsAccountChainList = this.list(new LambdaQueryWrapper<UmsAccountChain>()
                    .eq(UmsAccountChain::getMemberId, memberId)
                    .eq(UmsAccountChain::getChainType, chainForm.getChainType())
                    .eq(UmsAccountChain::getAddress, chainForm.getAddress())
            );
            if (umsAccountChainList.isEmpty()) {
                UmsAccountChain accountChain = new UmsAccountChain();
                accountChain.setAddress(chainForm.getAddress());
                accountChain.setChainType(chainForm.getChainType());
                accountChain.setMemberId(memberId);
                accountChain.setBankCardCode(chainForm.getBankCardCode());
                accountChain.setBankUsername(chainForm.getBankUsername());
                accountChain.setBankName(chainForm.getBankName());
                accountChain.setStatus(0);
                saveFlag = this.saveOrUpdate(accountChain);
            }
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return saveFlag;
    }

    /**
     * 解绑三方账户
     *
     * @param unbindForm
     * @return
     */
    @Override
    public boolean unbindAccount(AddressChainUnbindForm unbindForm, Long memberId) {
        boolean saveFlag = false;
        RLock lock = redissonClient.getLock(UmsConstants.USER_LOCK_BIND_CHAIN_ACCOUNT_PREFIX + memberId);
        try {
            lock.lock();
            //三方账号检测
            UmsAccountChain umsAccountChain = this.getById(unbindForm.getId());
            Assert.isTrue(umsAccountChain != null, "三方账号不存在");
            Assert.isTrue(umsAccountChain.getMemberId() == memberId, "非您的三方账号");
            Assert.isTrue(umsAccountChain.getChainType() != 0, "不能解绑此类账户");
            //交易密码验证
            UmsMember umsMember = memberService.getById(memberId);
            String tradePassword = umsMember.getTradePassword();
            if (tradePassword == null || tradePassword.equals("") == true) {
                throw new BizException("请先设置交易密码,再解绑定");
            }
            boolean check = MD5Util.encodeSaltMD5(unbindForm.getTradePassword(), umsMember.getSalt()).equals(tradePassword);
            if (!check) {
                throw new BizException(ResultCode.TRADE_PASSWORD_ERROR);
            }
            Integer ret = this.baseMapper.deleteById(unbindForm.getId());
//            log.info("删除三方账号返回{}", ret);
            if (ret == 1) {
                saveFlag = true;
            }
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return saveFlag;
    }

    /**
     * 修改三方账户
     *
     * @param chainForm
     * @return
     */
    @Override
    public boolean updateAccount(Long id, AddressChainForm chainForm, Long memberId) {
        //
        boolean status = false;
        RLock lock = redissonClient.getLock(UmsConstants.USER_LOCK_BIND_CHAIN_ACCOUNT_PREFIX + memberId);
        try {
            lock.lock();
            boolean b = VCodeUtils.checkVCode(stringRedisTemplate, VCodeTypeEnum.BIND_THIRD_PLATFORM, chainForm.getMobile(), chainForm.getCode());
            if (!b) {
                throw new BizException(ResultCode.VERIFY_CODE_ERROR);
            }
            status = this.update(new LambdaUpdateWrapper<UmsAccountChain>()
                    .eq(UmsAccountChain::getId, id)
                    .eq(UmsAccountChain::getMemberId, memberId)
                    .set(UmsAccountChain::getBankName, chainForm.getBankName())
                    .set(UmsAccountChain::getBankCardCode, chainForm.getBankCardCode())
                    .set(UmsAccountChain::getBankUsername, chainForm.getBankUsername())
                    .set(UmsAccountChain::getAddress, chainForm.getAddress()));

        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return status;
    }


}

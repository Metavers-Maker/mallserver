package com.muling.mall.ums.service.impl;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.cert.service.HttpApiClientSand;
import com.muling.common.enums.BusinessTypeEnum;
import com.muling.common.exception.BizException;
import com.muling.common.redis.utils.BusinessNoGenerator;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.ums.constant.UmsConstants;
import com.muling.mall.ums.converter.BankConverter;
import com.muling.mall.ums.mapper.UmsBankMapper;
import com.muling.mall.ums.pojo.entity.UmsBank;
import com.muling.mall.ums.pojo.form.*;
import com.muling.mall.ums.pojo.vo.BankBindVO;
import com.muling.mall.ums.pojo.vo.BankVO;
import com.muling.mall.ums.pojo.vo.MemberAuthVO;
import com.muling.mall.ums.service.IUmsBankService;
import com.muling.mall.ums.service.IUmsMemberAuthService;
import com.muling.mall.ums.service.IUmsMemberService;
import com.muling.mall.ums.util.BankCardInfoBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.UnsupportedEncodingException;
import java.util.List;


/**
 * 会员链上地址业务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UmsBankServiceImpl extends ServiceImpl<UmsBankMapper, UmsBank> implements IUmsBankService {

    private final RedissonClient redissonClient;

    private final BusinessNoGenerator businessNoGenerator;

    private final IUmsMemberService memberService;

    private final IUmsMemberAuthService authService;

    private final StringRedisTemplate stringRedisTemplate;

    private final HttpApiClientSand clientSand;

    private final Environment env;

    /**
     * 获取当前登录会员银行卡列表
     *
     * @return
     */
    @Override
    public List<BankVO> listBank(Integer platType) {
        Long memberId = MemberUtils.getMemberId();
        List<UmsBank> banks = this.list(new LambdaQueryWrapper<UmsBank>()
                .eq(UmsBank::getMemberId, memberId)
                .eq(UmsBank::getPlatType, platType)
                .orderByDesc(UmsBank::getCreated)
        );
        List<BankVO> bankVOList = BankConverter.INSTANCE.pos2vos(banks);
        return bankVOList;
    }

    /**
     * 绑定银行卡
     *
     * @param bankForm
     * @return
     */
    @Override
    public BankBindVO bindCard(BankBindForm bankForm, Long memberId) {
        RLock lock = redissonClient.getLock(UmsConstants.USER_LOCK_BIND_BANK_PREFIX + memberId);
        try {
            lock.lock();
            //
            MemberAuthVO authVO = authService.queryFullByMemberId(memberId);
            Assert.isTrue(authVO != null, "用户实名信息不正确");
            String realName = authVO.getRealName();
            String idCard = authVO.getIdCard();
            //
            UmsBank umsBank = this.baseMapper.selectOne(new LambdaQueryWrapper<UmsBank>()
                    .eq(UmsBank::getMemberId, memberId)
                    .eq(UmsBank::getPlatType, 0)
                    .eq(UmsBank::getBankCardCode, bankForm.getCardNo()));
            if (umsBank != null) {
                if (umsBank.getStatus() == 1) {
                    throw new BizException("该卡已经绑定，请不要重新绑定");
                }
            } else {
                umsBank = new UmsBank();
                umsBank.setUserId("sand" + memberId.toString());
                umsBank.setPlatType(0);
                umsBank.setMemberId(memberId);
                umsBank.setBankCardCode(bankForm.getCardNo());
                umsBank.setBankUsername(realName);
                //银行名称
//            umsBank.setBankName(bankForm.getBankName());
                umsBank.setStatus(0);
                //cardNum银行卡号
                BankCardInfoBean bankCardInfoBean = new BankCardInfoBean(bankForm.getCardNo());
                if (bankCardInfoBean != null) {
                    String cardType = bankCardInfoBean.getCardType();
                    String bankName = bankCardInfoBean.getBankName();
                    umsBank.setBankName(bankName);
                }
            }
            //
            String sandSN = businessNoGenerator.generate(BusinessTypeEnum.SAND) + "sand";
            JSONObject bodyParam = new JSONObject();
            bodyParam.put("userId", umsBank.getUserId());
            bodyParam.put("applyNo", sandSN);
            bodyParam.put("cardNo", bankForm.getCardNo());
            bodyParam.put("userName", realName);
            bodyParam.put("phoneNo", bankForm.getMobile());
            bodyParam.put("certificateType", "01");
            bodyParam.put("certificateNo", idCard);
            bodyParam.put("creditFlag", bankForm.getCardType());
            if (bankForm.getCardType().equals("2")) {
                bodyParam.put("checkNo", bankForm.getCvs());
                bodyParam.put("checkExpiry", bankForm.getCardExpire());
            } else {
                bodyParam.put("checkNo", "");
                bodyParam.put("checkExpiry", "");
            }
            bodyParam.put("extend", "");
            BankBindVO bindVO = new BankBindVO();
            JSONObject ret = clientSand.bindCard(bodyParam);
            if (ret != null) {
                JSONObject head = ret.getObject("head", JSONObject.class);
                JSONObject body = ret.getObject("body", JSONObject.class);
                String retCode = head.getString("respCode");
                String retMsg = head.getString("respMsg");
                if (retCode != null && retCode.equals("000000")) {
                    String sandSn = body.getString("sdMsgNo");
                    umsBank.setSandSn(sandSn);
                    boolean saveFlag = this.saveOrUpdate(umsBank);
                    if (saveFlag) {
                        bindVO.setBindSn(sandSn);
                    } else {
                        throw new BizException("绑卡本地异常");
                    }
                } else {
                    log.info("sand 申请绑卡异常{} {}", retCode, retMsg);
                    throw new BizException(retMsg);
                }
            }
            return bindVO;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 绑定卡片确认
     *
     * @param bankForm
     * @return
     */
    @Override
    public boolean bindCardEnsure(BankBindEnsureForm bankForm, Long memberId) {
        boolean updateRet = false;
        UmsBank umsBank = this.baseMapper.selectOne(new LambdaQueryWrapper<UmsBank>()
                .eq(UmsBank::getSandSn, bankForm.getSdMsgNo()));
        Assert.isTrue(umsBank != null, "银行卡不存在，请重新申请");
        //确认绑卡
        RLock lock = redissonClient.getLock(UmsConstants.USER_LOCK_BIND_BANK_PREFIX + memberId);
        try {
            lock.lock();
            //
            JSONObject bodyParam = new JSONObject();
            bodyParam.put("userId", umsBank.getUserId());
            bodyParam.put("sdMsgNo", umsBank.getSandSn());
            bodyParam.put("phoneNo", bankForm.getMobile());
            bodyParam.put("smsCode", bankForm.getCode());
            //
            JSONObject ret = clientSand.bindCardEnsure(bodyParam);
            if (ret != null) {
                JSONObject head = ret.getObject("head", JSONObject.class);
                JSONObject body = ret.getObject("body", JSONObject.class);
                String retCode = head.getString("respCode");
                String retMsg = head.getString("respMsg");
                if (retCode != null && retCode.equals("000000")) {
                    String bid = body.getString("bid");
                    umsBank.setBid(bid);
                    umsBank.setStatus(1);
                    boolean saveFlag = this.saveOrUpdate(umsBank);
                    return saveFlag;
                } else {
                    log.info("sand 确认绑卡异常{} {}", retCode, retMsg);
                    throw new BizException(retMsg);
                }
            }
            return updateRet;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 解绑银行卡
     *
     * @param id
     * @return
     */
    @Override
    public boolean unbindCard(Long id, Long memberId) {
        //
        RLock lock = redissonClient.getLock(UmsConstants.USER_LOCK_BIND_CHAIN_ACCOUNT_PREFIX + memberId);
        try {
            lock.lock();
            UmsBank umsBank = this.getById(id);
            Assert.isTrue(umsBank.getMemberId().equals(memberId), "非您的银行卡");
            //
            String sandSN = businessNoGenerator.generate(BusinessTypeEnum.SAND) + "sand";
            JSONObject bodyParam = new JSONObject();
            bodyParam.put("userId", umsBank.getUserId());
            bodyParam.put("applyNo", sandSN);
            bodyParam.put("bid", umsBank.getBid());
            bodyParam.put("extend", "01");
            boolean isDev = ArrayUtil.contains(env.getActiveProfiles(), "dev");
            if (isDev) {
                //开发版本回调
                bodyParam.put("notifyUrl", "https://i72558b438.zicp.fun/app-api/v1/bank/account/sand/callback");
            } else {
                //主网回调
                bodyParam.put("notifyUrl", "https://link2meta-api.link2meta.cn/app-api/v1/bank/account/sand/callback");
            }
            //
            JSONObject ret = clientSand.unbindCard(bodyParam);
            if (ret != null) {
                JSONObject head = ret.getObject("head", JSONObject.class);
                JSONObject body = ret.getObject("body", JSONObject.class);
                String retCode = head.getString("respCode");
                String retMsg = head.getString("respMsg");
                if (retCode != null && retCode.equals("000000")) {
                    umsBank.setStatus(2);
                    boolean saveFlag = this.saveOrUpdate(umsBank);
                    return saveFlag;
                } else {
                    log.info("sand 确认绑卡异常{} {}", retCode, retMsg);
                    throw new BizException(retMsg);
                }
            }
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return false;
    }

    /**
     * 设置默认卡片
     *
     * @param id
     * @return
     */
    @Override
    public boolean usedBank(Long id, Long memberId) {
        //
        boolean retUpdate = false;
        RLock lock = redissonClient.getLock(UmsConstants.USER_LOCK_BIND_BANK_PREFIX + memberId);
        try {
            lock.lock();
            UmsBank umsBank = this.baseMapper.selectOne(new LambdaQueryWrapper<UmsBank>()
                    .eq(UmsBank::getMemberId, memberId)
                    .eq(UmsBank::getUsed, 1));
            if (umsBank != null) {
                if (umsBank.getId().equals(id)) {
                    throw new BizException("此卡是默认卡片，不要重复设置");
                } else {
                    umsBank.setUsed(0);
                    this.updateById(umsBank);
                }
            }
            //
            retUpdate = this.update(new LambdaUpdateWrapper<UmsBank>()
                    .eq(UmsBank::getId, id)
                    .eq(UmsBank::getMemberId, memberId)
                    .set(UmsBank::getUsed, 1));
            return retUpdate;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}

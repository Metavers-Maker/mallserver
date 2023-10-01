package com.muling.mall.ums.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.muling.common.auth.GoogleAuthenticator;
import com.muling.common.base.IBaseEnum;
import com.muling.common.cert.service.HttpApiClientWechat;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.enums.VCodeTypeEnum;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.common.util.IdUtils;
import com.muling.common.util.MD5Util;
import com.muling.common.util.VCodeUtils;
import com.muling.common.web.util.MemberUtils;
import com.muling.common.web.util.RequestUtils;
import com.muling.mall.ums.constant.UmsConstants;
import com.muling.mall.ums.converter.MemberConverter;
import com.muling.mall.ums.enums.MemberStatusEnum;
import com.muling.mall.ums.event.MemberRegisterEvent;
import com.muling.mall.ums.event.OhMemberRegisterEvent;
import com.muling.mall.ums.mapper.UmsUserMapper;
import com.muling.mall.ums.pojo.app.MemberSearchDTO;
import com.muling.mall.ums.pojo.dto.*;
import com.muling.mall.ums.pojo.entity.UmsAccountChain;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.pojo.entity.UmsMemberInvite;
import com.muling.mall.ums.pojo.form.*;
import com.muling.mall.ums.pojo.vo.MemberSimpleVO;
import com.muling.mall.ums.pojo.vo.MemberVO;
import com.muling.mall.ums.service.IUmsMemberInviteService;
import com.muling.mall.ums.service.IUmsMemberService;
//import com.muling.mall.ums.util.ChainOperateUtils;
import com.muling.common.util.InviteCodeUtil;
import com.muling.mall.wms.api.WalletFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import com.alibaba.cloudapi.sdk.client.ApacheHttpClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.muling.common.constant.GlobalConstants.STATUS_YES;

@Service
@Slf4j
@RequiredArgsConstructor
public class UmsMemberServiceImpl extends ServiceImpl<UmsUserMapper, UmsMember> implements IUmsMemberService {

    private final RabbitTemplate rabbitTemplate;

    private final RedissonClient redissonClient;

    private final StringRedisTemplate stringRedisTemplate;

    private final IUmsMemberInviteService memberInviteService;

    private final WalletFeignClient walletFeignClient;

    private final HttpApiClientWechat httpApiClientWechat;

    private final Environment env;


    /**
     * 系统注册时候自动新增会员
     *
     * @param memberDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addMember(MemberDTO memberDTO) {
        UmsMember umsMember = MemberConverter.INSTANCE.dto2Po(memberDTO);
        umsMember = buildCommonMember(umsMember);
        //自动生成密码
        umsMember.setPassword(MD5Util.encodeSaltMD5(RandomUtil.randomString(6), umsMember.getSalt()));
//        //自动生成交易密码
//        umsMember.setTradePassword(MD5Util.encodeSaltMD5(RandomUtil.randomString(6), umsMember.getSalt()));
        boolean result = this.save(umsMember);
        Assert.isTrue(result, "新增会员失败");
        return umsMember.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindWxopenDirect(BindWxopenForm wxopenForm) {
        Long memberId = MemberUtils.getMemberId();
        boolean retFlag = false;
        RLock lock = redissonClient.getLock(UmsConstants.USER_LOCK_REGISTER_PREFIX + memberId);
        try {
            lock.lock();
            //TODO 此处可以缓存优化
            UmsMember umsMember = this.getOne(new LambdaQueryWrapper<UmsMember>().eq(UmsMember::getId, memberId));
            if (umsMember == null) {
                throw new BizException(ResultCode.USER_ALREADY_EXIST);
            }
            if (umsMember.getOpenid() != null) {
                throw new BizException("已经绑定微信");
            }
            boolean isDev = ArrayUtil.contains(env.getActiveProfiles(), "dev");
            boolean jump = false;
            if (isDev) {
                //开发版本
                if (wxopenForm.getVerifyCode().equals("6666")) {
                    jump = true;
                }
            }
            //验证
            if (!jump) {
                boolean b = VCodeUtils.checkVCode(stringRedisTemplate, VCodeTypeEnum.BIND_THIRD_PLATFORM, umsMember.getMobile(), wxopenForm.getVerifyCode());
                if (!b) {
                    throw new BizException(ResultCode.VERIFY_CODE_ERROR);
                }
            }
            //获取OpenId
            JSONObject ret = httpApiClientWechat.openLogin(wxopenForm.getWxopenCode());
            if (ret.get("code").equals(200)) {
                if (ret.get("errcode") != null) {
                    //微信获取code异常
                    throw new BizException(ResultCode.WXOPEN_AUTH_ERROR);
                } else {
                    //正常获取OpenId
                    String openid = ret.get("openid").toString();
                    umsMember.setOpenid(openid);
                    retFlag = this.updateById(umsMember);
                }
            } else {
                //微信获取code异常
                log.info("wxopen catch error{}", ret.get("code").toString());
                throw new BizException(ResultCode.WXOPEN_AUTH_ERROR);
            }
        } catch (Exception e) {
            //
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return retFlag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unbindWxopenDirect(BindWxopenForm wxopenForm) {
        Long memberId = MemberUtils.getMemberId();
        boolean retFlag = false;
        RLock lock = redissonClient.getLock(UmsConstants.USER_LOCK_REGISTER_PREFIX + memberId);
        try {
            lock.lock();
            //TODO 此处可以缓存优化
            UmsMember umsMember = this.getOne(new LambdaQueryWrapper<UmsMember>().eq(UmsMember::getId, memberId));
            if (umsMember == null) {
                throw new BizException(ResultCode.USER_ALREADY_EXIST);
            }
            if (umsMember.getOpenid() != null) {
                throw new BizException("已经绑定微信");
            }
            boolean isDev = ArrayUtil.contains(env.getActiveProfiles(), "dev");
            boolean jump = false;
            if (isDev) {
                //开发版本
                if (wxopenForm.getVerifyCode().equals("6666")) {
                    jump = true;
                }
            }
            //验证
            if (!jump) {
                boolean b = VCodeUtils.checkVCode(stringRedisTemplate, VCodeTypeEnum.BIND_THIRD_PLATFORM, umsMember.getMobile(), wxopenForm.getVerifyCode());
                if (!b) {
                    throw new BizException(ResultCode.VERIFY_CODE_ERROR);
                }
            }
            umsMember.setOpenid(null);
            retFlag = this.updateById(umsMember);
        } catch (Exception e) {
            //
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return retFlag;
    }

    /**
     * 绑定微信开放平台
     *
     * @param wxopenDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindWxopen(BindWxopenDTO wxopenDTO) {
        LambdaUpdateWrapper<UmsMember> updateWrapper = new LambdaUpdateWrapper<UmsMember>()
                .eq(UmsMember::getMobile, wxopenDTO.getMobile())
                .set(wxopenDTO.getOpenId() != null, UmsMember::getOpenid, wxopenDTO.getOpenId());
        return this.update(updateWrapper);
    }

    /**
     * 绑定支付宝
     *
     * @param alipayDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindAlipay(BindAlipayDTO alipayDTO) {
        LambdaUpdateWrapper<UmsMember> updateWrapper = new LambdaUpdateWrapper<UmsMember>()
                .eq(UmsMember::getMobile, alipayDTO.getMobile())
                .set(alipayDTO.getAlipayId() != null, UmsMember::getAlipayId, alipayDTO.getAlipayId());
        return this.update(updateWrapper);
    }

    private static final int INVITE_CODE_LENGTH = 9; // 邀请码长度
    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyz"; // 可选字符集
    private static final int BASE = CHARACTERS.length(); // 可选字符集的长度

    protected UmsMember buildCommonMember(UmsMember umsMember) {
        String uid = IdUtils.makeId();
        umsMember.setUid(uid);
        //生成用户ID
        umsMember.setNickName("用户#" + uid);
        //Long.parseLong(uid)
        //生成邀请码
        long number = System.currentTimeMillis() - 1640966400000L;
        String inviteCode = InviteCodeUtil.makeCodeByUIDUnique(number);
        umsMember.setInviteCode(inviteCode);
        //生成用户状态
        umsMember.setStatus(MemberStatusEnum.COMMON.getValue());
        //设置google状态
        umsMember.setIsBindGoogle(GlobalConstants.STATUS_NO);
        umsMember.setSecret(GoogleAuthenticator.generateSecretKey());
        //生成salt
        umsMember.setSalt(RandomUtil.randomString(6));
        //umsMember.setChainAddress(ChainOperateUtils.createAccount());
        return umsMember;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long adminAddMember(AdminRegisterForm registerForm) {
        //记得加锁
        RLock lock = redissonClient.getLock(UmsConstants.USER_LOCK_REGISTER_PREFIX + registerForm.getMobile());
        try {
            lock.lock();
            //TODO 此处可以缓存优化
            boolean exists = this.baseMapper.exists(new LambdaQueryWrapper<UmsMember>()
                    .eq(UmsMember::getMobile, registerForm.getMobile())
            );
            if (exists) {
                throw new BizException(ResultCode.USER_ALREADY_EXIST);
            }
            Long inviteUserId = null;
            UmsMember inviteMember = null;
            String inviteCode = registerForm.getInviteCode();
            if (StrUtil.isNotBlank(inviteCode)) {
                //TODO 此处可以缓存优化
                UmsMemberInvite memberInvite = memberInviteService.getByInviteCode(inviteCode);
                if (memberInvite == null) {
                    throw new BizException(ResultCode.USER_INVITE_CODE_NOT_EXIST);
                } else {
                    inviteUserId = memberInvite.getMemberId();
                }
                inviteMember = getById(inviteUserId);
            }
            //增加被封会员，无法邀请用户注册
            Assert.isTrue(inviteMember != null, "邀请会员不存在");
            Assert.isTrue(inviteMember.getStatus().intValue() != 0, "邀请码失效01");
            UmsMember umsMember = buildCommonMember(new UmsMember());
            umsMember.setMobile(registerForm.getMobile());
            umsMember.setPassword(MD5Util.encodeSaltMD5(RandomUtil.randomString(6), umsMember.getSalt()));
//            umsMember.setTradePassword(MD5Util.encodeSaltMD5(RandomUtil.randomString(6), umsMember.getSalt()));
            //
            boolean result = this.save(umsMember);
            Assert.isTrue(result, "新增会员失败");
            MemberRegisterDTO memberDTO = MemberConverter.INSTANCE.po2registerDTO(umsMember);
            MemberRegisterDTO inviteDTO = MemberConverter.INSTANCE.po2registerDTO(inviteMember);
            MemberRegisterEvent registerEvent = new MemberRegisterEvent()
                    .setMember(memberDTO)
                    .setInviteMember(inviteDTO);
            rabbitTemplate.convertAndSend(GlobalConstants.MQ_MEMBER_REGISTER_QUEUE, JSONUtil.toJsonStr(registerEvent));
            return umsMember.getId();
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(RegisterForm registerForm) {
        String mobile = registerForm.getMobile();
        String[] prefix = {"162", "165", "166", "167", "170", "171"};
        Arrays.stream(prefix).forEach(s -> {
            if (mobile.startsWith(s)) {
                throw new BizException(ResultCode.PARAM_ERROR, "虚拟号不允许使用");
            }
        });
        //记得加锁
        RLock lock = redissonClient.getLock(UmsConstants.USER_LOCK_REGISTER_PREFIX + mobile);
        try {
            lock.lock();
            //TODO 此处可以缓存优化
            boolean exists = this.baseMapper.exists(new LambdaQueryWrapper<UmsMember>()
                    .eq(UmsMember::getMobile, mobile)
            );
            if (exists) {
                throw new BizException(ResultCode.USER_ALREADY_EXIST);
            }
            boolean isDev = ArrayUtil.contains(env.getActiveProfiles(), "dev");
            boolean jump = false;
            if (isDev) {
                //开发版本
                if (registerForm.getCode().equals("6666")) {
                    jump = true;
                }
            }
            //验证
            if (!jump) {
                boolean b = VCodeUtils.checkVCode(stringRedisTemplate, VCodeTypeEnum.REGISTER, mobile, registerForm.getCode());
                if (!b) {
                    throw new BizException(ResultCode.VERIFY_CODE_ERROR);
                }
            }
            //
            String inviteCode = registerForm.getInviteCode();
            //TODO 此处可以缓存优化
            UmsMember umsMember = buildCommonMember(new UmsMember());
            umsMember.setMobile(mobile);
            umsMember.setPassword(MD5Util.encodeSaltMD5(registerForm.getPassword(), umsMember.getSalt()));
//            umsMember.setTradePassword(MD5Util.encodeSaltMD5(registerForm.getTradePassword(), umsMember.getSalt()));
            umsMember.setIsOh(0);
            String deviceId = RequestUtils.getDeviceId();
            umsMember.setDeviceId(deviceId);
            boolean result = this.save(umsMember);
            Assert.isTrue(result, "新增会员失败");
            //邀请人存在 走下面相关逻辑
            UmsMember inviteMember = getByInviteCode(inviteCode);
            if (inviteMember != null) {
                MemberRegisterDTO memberDTO = MemberConverter.INSTANCE.po2registerDTO(umsMember);
                MemberRegisterDTO inviteDTO = MemberConverter.INSTANCE.po2registerDTO(inviteMember);
                MemberRegisterEvent registerEvent = new MemberRegisterEvent()
                        .setMember(memberDTO)
                        .setInviteMember(inviteDTO);
                rabbitTemplate.convertAndSend(GlobalConstants.MQ_MEMBER_REGISTER_QUEUE, JSONUtil.toJsonStr(registerEvent));
            }
            return result;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unregister(UnRegisterForm form) {
        Long memberId = MemberUtils.getMemberId();
        boolean isDev = ArrayUtil.contains(env.getActiveProfiles(), "dev");
        boolean jump = false;
        if (isDev) {
            //开发版本
            if (form.getCode().equals("6666")) {
                jump = true;
            }
        }
        //验证
        if (!jump) {
            boolean b = VCodeUtils.checkVCode(stringRedisTemplate, VCodeTypeEnum.UN_REGISTER, form.getMobile(), form.getCode());
            if (!b) {
                throw new BizException(ResultCode.VERIFY_CODE_ERROR);
            }
        }
        //
        boolean ret = this.update(new LambdaUpdateWrapper<UmsMember>()
                .eq(UmsMember::getId, memberId)
                .set(UmsMember::getDeleted, STATUS_YES));
        return ret;
    }

    @Override
    public boolean resetPassword(ResetPasswordForm resetPasswordForm) {
        String mobile = resetPasswordForm.getMobile();
        Integer type = resetPasswordForm.getType();
        String code = resetPasswordForm.getCode();
        String password = resetPasswordForm.getPassword();
        VCodeTypeEnum value = IBaseEnum.getEnumByValue(type, VCodeTypeEnum.class);
        if (value == null) {
            throw new BizException(ResultCode.PARAM_ERROR, "验证码类型不匹配");
        }
        if (value != VCodeTypeEnum.RESET_PASSWORD && value != VCodeTypeEnum.RESET_TRADE_PASSWORD) {
            throw new BizException(ResultCode.PARAM_ERROR, "验证码类型不匹配");
        }
        MemberAuthDTO umsMember = getByMobile(mobile);
        if (umsMember == null) {
            throw new BizException(ResultCode.USER_NOT_EXIST);
        }
        Long memberId = umsMember.getMemberId();
        boolean b = VCodeUtils.checkVCode(stringRedisTemplate, value, mobile, code);
        if (!b) {
            throw new BizException(ResultCode.VERIFY_CODE_ERROR);
        }
        boolean update = false;
        if (value == VCodeTypeEnum.RESET_PASSWORD) {
            update = update(Wrappers.<UmsMember>lambdaUpdate()
                    .set(UmsMember::getPassword, MD5Util.encodeSaltMD5(password, umsMember.getSalt()))
                    .eq(UmsMember::getId, memberId));
        } else if (value == VCodeTypeEnum.RESET_TRADE_PASSWORD) {
            update = update(Wrappers.<UmsMember>lambdaUpdate()
                    .set(UmsMember::getTradePassword, MD5Util.encodeSaltMD5(password, umsMember.getSalt()))
                    .eq(UmsMember::getId, memberId));
        }
        return update;
    }

    @Override
    public boolean resetTradePassword(ResetTradePasswordForm tradePasswordForm) {
        //
        String mobile = tradePasswordForm.getMobile();
        String code = tradePasswordForm.getCode();
        String password = tradePasswordForm.getTradePassword();
        MemberAuthDTO umsMember = getByMobile(mobile);
        if (umsMember == null) {
            throw new BizException(ResultCode.USER_NOT_EXIST);
        }
        //手机验证码-验证
        Long memberId = umsMember.getMemberId();
        boolean b = VCodeUtils.checkVCode(stringRedisTemplate, VCodeTypeEnum.RESET_TRADE_PASSWORD, mobile, code);
        if (!b) {
            throw new BizException(ResultCode.VERIFY_CODE_ERROR);
        }
        boolean update = update(Wrappers.<UmsMember>lambdaUpdate()
                .set(UmsMember::getTradePassword, MD5Util.encodeSaltMD5(password, umsMember.getSalt()))
                .eq(UmsMember::getId, memberId));
        return update;
    }

    @Override
    public boolean checkTradePassword(String password) {
        Long memberId = MemberUtils.getMemberId();
        boolean retFlag = false;
        RLock lock = redissonClient.getLock(UmsConstants.USER_LOCK_TRADE_PASSWORD_PREFIX + memberId);
        try {
            lock.lock();
            UmsMember umsMember = this.getById(memberId);
            String tradePassword = umsMember.getTradePassword();
            if (tradePassword == null || tradePassword.equals("") == true) {
                throw new BizException("请先设置交易密码");
            }
            boolean check = MD5Util.encodeSaltMD5(password, umsMember.getSalt()).equals(tradePassword);
            log.info("检测交易密码{} {} {}", password, tradePassword, check);
            Assert.isTrue(check, "交易密码错误");
//            if (!check) {
//                throw new BizException(ResultCode.TRADE_PASSWORD_ERROR);
//            }
            //验证通过，将交易密码放置到redis中缓存
            //
            retFlag = true;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return retFlag;
    }

    @Override
    public IPage<UmsMember> list(Page<UmsMember> page, String nickname, String mobile) {
        List<UmsMember> list = this.baseMapper.list(page, nickname, mobile);
        page.setRecords(list);
        return page;
    }

    @Cacheable(cacheNames = "cache_ums", key = "'user_'+#page.current+'_'+#page.size+'_' + #name")
    @Override
    public IPage<MemberSearchDTO> search(Page<MemberSearchDTO> page, String name) {
        MPJLambdaWrapper<MemberSearchDTO> queryWrapper = new MPJLambdaWrapper<MemberSearchDTO>()
                .select(UmsMember::getId, UmsMember::getAvatarUrl, UmsMember::getNickName)
                .like(StringUtils.isNotBlank(name), UmsMember::getNickName, name);
        IPage<MemberSearchDTO> p = this.baseMapper.selectJoinPage(page, MemberSearchDTO.class, queryWrapper);
        return p;
    }

    @Cacheable(cacheNames = "cache_ums_simple", key = "#memberId")
    @Override
    public MemberSimpleDTO getSimpleById(Long memberId) {
        UmsMember umsMember = this.getOne(new LambdaQueryWrapper<UmsMember>()
                .eq(UmsMember::getId, memberId)
                .select(UmsMember::getId,
                        UmsMember::getNickName,
                        UmsMember::getAvatarUrl,
                        UmsMember::getWechat,
                        UmsMember::getExt
                )
        );
        return MemberConverter.INSTANCE.po2simpleDTO(umsMember);
    }

    @Override
    public MemberVO getCurrentMemberInfo() {
        Long memberId = MemberUtils.getMemberId();
        UmsMember umsMember = this.getOne(new LambdaQueryWrapper<UmsMember>()
                .eq(UmsMember::getId, memberId)
                .select(UmsMember::getId,
                        UmsMember::getUid,
                        UmsMember::getOpenid,
                        UmsMember::getAlipay,
                        UmsMember::getNickName,
                        UmsMember::getAvatarUrl,
                        UmsMember::getEmail,
                        UmsMember::getMobile,
                        UmsMember::getWechat,
                        UmsMember::getChainAddress,
                        UmsMember::getStatus,
                        UmsMember::getAuthStatus,
                        UmsMember::getExt,
                        UmsMember::getInviteCode,
                        UmsMember::getSafeCode,
                        UmsMember::getCreated,
                        UmsMember::getDeleted,
                        UmsMember::getTradePassword
                )
        );
        MemberVO memberVO = MemberConverter.INSTANCE.po2vo(umsMember);
        if (umsMember.getTradePassword() != null) {
            memberVO.setHasTradeCode(true);
        } else {
            memberVO.setHasTradeCode(false);
        }
        if (umsMember.getOpenid() != null) {
            memberVO.setHasWx(true);
        } else {
            memberVO.setHasWx(false);
        }
        if (umsMember.getAlipayId() != null) {
            memberVO.setHasAlipay(true);
        } else {
            memberVO.setHasAlipay(false);
        }
        return memberVO;
    }

    @Override
    public MemberSimpleVO getMemberInfoByUid(String uid) {
        UmsMember umsMember = this.getOne(new LambdaQueryWrapper<UmsMember>()
                .eq(UmsMember::getUid, uid)
                .select(UmsMember::getId,
                        UmsMember::getUid,
                        UmsMember::getNickName,
                        UmsMember::getEmail,
                        UmsMember::getWechat,
                        UmsMember::getChainAddress,
                        UmsMember::getSafeCode,
                        UmsMember::getDeleted
                )
        );
        return MemberConverter.INSTANCE.po2SimpleVo(umsMember);
    }

    @Override
    public MemberSimpleVO getMemberInfoById(Long id) {
        UmsMember umsMember = this.getById(id);
        if (umsMember.getDeleted() == STATUS_YES) {
            return null;
        }
        return MemberConverter.INSTANCE.po2SimpleVo(umsMember);
    }

    /**
     * 根据 openid 获取会员认证信息
     *
     * @param openid
     * @return
     */
    @Override
    public MemberAuthDTO getByOpenid(String openid) {
        UmsMember member = this.getOne(new LambdaQueryWrapper<UmsMember>()
                .eq(UmsMember::getOpenid, openid)
                .select(UmsMember::getId,
                        UmsMember::getNickName,
                        UmsMember::getStatus,
                        UmsMember::getEmail,
                        UmsMember::getIsBindGoogle,
                        UmsMember::getSecret,
                        UmsMember::getSalt,
                        UmsMember::getDeleted
                )
        );
        return MemberConverter.INSTANCE.po2authDTO(member);
    }

    /**
     * 根据 alipayId 获取会员认证信息
     *
     * @param alipayId
     * @return
     */
    @Override
    public MemberAuthDTO getByAlipayId(String alipayId) {
        UmsMember member = this.getOne(new LambdaQueryWrapper<UmsMember>()
                .eq(UmsMember::getAlipayId, alipayId)
                .select(UmsMember::getId,
                        UmsMember::getNickName,
                        UmsMember::getStatus,
                        UmsMember::getEmail,
                        UmsMember::getIsBindGoogle,
                        UmsMember::getSecret,
                        UmsMember::getSalt,
                        UmsMember::getDeleted
                )
        );
        return MemberConverter.INSTANCE.po2authDTO(member);
    }

    /**
     * 根据手机号获取会员认证信息
     *
     * @param mobile
     * @return
     */
    @Override
    public MemberAuthDTO getByMobile(String mobile) {
        UmsMember member = this.getOne(new LambdaQueryWrapper<UmsMember>()
                .eq(UmsMember::getMobile, mobile)
                .select(UmsMember::getId,
                        UmsMember::getNickName,
                        UmsMember::getEmail,
                        UmsMember::getStatus,
                        UmsMember::getIsBindGoogle,
                        UmsMember::getSecret,
                        UmsMember::getSalt,
                        UmsMember::getPassword,
                        UmsMember::getDeleted
                )
        );
        return MemberConverter.INSTANCE.po2authDTO(member);
    }

    /**
     * 根据 email 获取会员认证信息
     *
     * @param email
     * @return
     */
    @Override
    public MemberAuthDTO getByEmail(String email) {
        UmsMember member = this.getOne(new LambdaQueryWrapper<UmsMember>()
                .eq(UmsMember::getEmail, email)
                .select(UmsMember::getId,
                        UmsMember::getNickName,
                        UmsMember::getStatus,
                        UmsMember::getIsBindGoogle,
                        UmsMember::getSecret,
                        UmsMember::getSalt,
                        UmsMember::getPassword,
                        UmsMember::getDeleted
                )
        );
        return MemberConverter.INSTANCE.po2authDTO(member);
    }

    /**
     * 根据 username 获取会员认证信息
     *
     * @param username
     * @return
     */
    @Override
    public MemberAuthDTO getByUsername(String username) {
        UmsMember member = this.getOne(new LambdaQueryWrapper<UmsMember>()
                .eq(UmsMember::getNickName, username)
                .select(UmsMember::getId,
                        UmsMember::getNickName,
                        UmsMember::getEmail,
                        UmsMember::getStatus,
                        UmsMember::getIsBindGoogle,
                        UmsMember::getSecret,
                        UmsMember::getSalt,
                        UmsMember::getPassword,
                        UmsMember::getDeleted
                )
        );
        return MemberConverter.INSTANCE.po2authDTO(member);
    }

    /**
     * 根据 uid 获取会员认证信息
     *
     * @param uid
     * @return
     */
    @Override
    public MemberAuthDTO getByUId(String uid) {
        UmsMember member = this.getOne(new LambdaQueryWrapper<UmsMember>()
                .eq(UmsMember::getUid, uid)
                .select(UmsMember::getId,
                        UmsMember::getNickName,
                        UmsMember::getEmail,
                        UmsMember::getStatus,
                        UmsMember::getIsBindGoogle,
                        UmsMember::getSecret,
                        UmsMember::getSalt,
                        UmsMember::getDeleted
                )
        );
        return MemberConverter.INSTANCE.po2authDTO(member);
    }

    /**
     * 根据 inviteCode 获取会员信息
     *
     * @param inviteCode
     * @return
     */
    @Override
    public UmsMember getByInviteCode(String inviteCode) {
        UmsMember member = this.getOne(new LambdaQueryWrapper<UmsMember>()
                .eq(UmsMember::getInviteCode, inviteCode)
                .select(UmsMember::getId,
                        UmsMember::getNickName,
                        UmsMember::getEmail,
                        UmsMember::getStatus,
                        UmsMember::getIsBindGoogle,
                        UmsMember::getSecret,
                        UmsMember::getSalt
                )
        );
        return member;
    }

    @Override
    public boolean feng(Long memberId, Integer status) {
        boolean f = memberInviteService.feng(memberId, status);
        if (f) {
            walletFeignClient.feng(memberId, status);
        }
        return f;
    }

    public static void main(String[] args) {

        String[] prefix = {"162", "165", "167", "170", "171"};
        String mobile = "16201075793";
        Arrays.stream(prefix).forEach(s -> {
            if (mobile.startsWith(s)) {
                throw new BizException(ResultCode.PARAM_ERROR, "虚拟号不允许使用");
            }
        });

    }
}

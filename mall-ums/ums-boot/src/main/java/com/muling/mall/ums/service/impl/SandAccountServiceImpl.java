package com.muling.mall.ums.service.impl;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.cert.service.HttpApiClientSand;
import com.muling.common.enums.BusinessTypeEnum;
import com.muling.common.exception.BizException;
import com.muling.common.redis.utils.BusinessNoGenerator;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.ums.mapper.UmsSandAccountMapper;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.pojo.entity.UmsSandAccount;
import com.muling.mall.ums.service.ISandAccountService;
import com.muling.mall.ums.service.IUmsMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class SandAccountServiceImpl extends ServiceImpl<UmsSandAccountMapper, UmsSandAccount> implements ISandAccountService {

    private final BusinessNoGenerator businessNoGenerator;

    private final IUmsMemberService memberService;

    private final HttpApiClientSand clientSand;

    private final Environment env;

    /**
     * 创建sand账户
     *
     * @return
     */
    @Transactional
    @Override
    public String createSandAccount(String ipAddr, Long testId) {
        Long memberId = MemberUtils.getMemberId();
        if (testId != null) {
            memberId = testId;
        }
        UmsMember member = memberService.getById(memberId);
        if (member == null) {
            throw new BizException("用户不存在");
        }
        //
        UmsSandAccount sandAccount = this.baseMapper.selectOne(new LambdaQueryWrapper<UmsSandAccount>()
                .eq(UmsSandAccount::getMemberId, memberId));
        if (sandAccount != null && sandAccount.getStatus() == 1) {
            throw new BizException("已经开通Sand账户");
        }
        //创建sand账号
        boolean isDev = ArrayUtil.contains(env.getActiveProfiles(), "dev");
        String sandSN = businessNoGenerator.generate(BusinessTypeEnum.SAND) + "sand";
        String url = clientSand.createAccount(ipAddr, member.getUid(), member.getNickName(), sandSN, isDev);
        if (url != null) {
            if (sandAccount == null) {
                sandAccount = new UmsSandAccount();
            }
            sandAccount.setMemberId(memberId);
            sandAccount.setUserId(member.getUid());
            sandAccount.setNickName(member.getNickName());
            sandAccount.setOrderSn(sandSN);
            sandAccount.setStatus(0);
            boolean ret = this.saveOrUpdate(sandAccount);
            if (!ret) {
                throw new BizException("创建sand账号失败");
            }
        }
        return url;
    }


}

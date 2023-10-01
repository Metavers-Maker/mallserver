package com.muling.mall.ums.es.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.StrUtil;
import com.muling.common.base.IBaseEnum;
import com.muling.mall.ums.enums.AuthStatusEnum;
import com.muling.mall.ums.es.entity.UmsMemberAuth;
import com.muling.mall.ums.es.repository.UmsMemberAuthRepository;
import com.muling.mall.ums.es.service.IUmsMemberAuthEService;
import com.muling.mall.ums.pojo.vo.MemberAuthVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UmsMemberAuthEServiceImpl implements IUmsMemberAuthEService {

    private final ElasticsearchRestTemplate elasticsearchRestTemple;

    private final UmsMemberAuthRepository memberAuthRepository;

    @Override
    public MemberAuthVO queryByMemberId(Long memberId) {
        UmsMemberAuth memberAuth = memberAuthRepository.findById(memberId).orElse(null);
//        MemberAuthVO memberAuthVO = MemberAuthConverter.INSTANCE.po2vo(memberAuth);
        MemberAuthVO memberAuthVO = new MemberAuthVO();
        BeanUtil.copyProperties(memberAuth, memberAuthVO);
        if (memberAuthVO != null) {
            memberAuthVO.setIdCard(IdcardUtil.hide(memberAuthVO.getIdCard(), 14, 18));
            memberAuthVO.setRealName(StrUtil.hide(memberAuthVO.getRealName(), 2, 3));
            memberAuthVO.setStatus(IBaseEnum.getEnumByValue(memberAuth.getStatus(), AuthStatusEnum.class));
        }
        return memberAuthVO;
    }
}

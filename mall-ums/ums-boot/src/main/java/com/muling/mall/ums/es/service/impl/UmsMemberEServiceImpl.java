package com.muling.mall.ums.es.service.impl;


import com.muling.common.web.util.MemberUtils;
import com.muling.mall.ums.converter.MemberConverter;
import com.muling.mall.ums.es.entity.UmsMember;
import com.muling.mall.ums.es.repository.UmsMemberRepository;
import com.muling.mall.ums.es.service.IUmsMemberEService;
import com.muling.mall.ums.pojo.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UmsMemberEServiceImpl implements IUmsMemberEService {

    private final ElasticsearchRestTemplate elasticsearchRestTemple;

    private final UmsMemberRepository memberRepository;

    @Override
    public MemberVO getCurrentMemberInfo() {
        Long memberId = MemberUtils.getMemberId();
        UmsMember umsMember = memberRepository.findById(memberId).orElse(null);
        MemberVO memberVO = MemberConverter.INSTANCE.po2vo(umsMember);
        return memberVO;
    }
}

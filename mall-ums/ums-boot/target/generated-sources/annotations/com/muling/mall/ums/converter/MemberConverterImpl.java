package com.muling.mall.ums.converter;

import cn.hutool.json.JSONObject;
import com.muling.mall.ums.pojo.dto.MemberAuthDTO;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import com.muling.mall.ums.pojo.dto.MemberRegisterDTO;
import com.muling.mall.ums.pojo.dto.MemberSimpleDTO;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.pojo.vo.MemberCoinRank;
import com.muling.mall.ums.pojo.vo.MemberSimpleVO;
import com.muling.mall.ums.pojo.vo.MemberVO;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-06-14T23:25:17+0800",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 14.0.1 (Oracle Corporation)"
)
@Component
public class MemberConverterImpl implements MemberConverter {

    @Override
    public MemberVO po2vo(UmsMember member) {
        if ( member == null ) {
            return null;
        }

        MemberVO memberVO = new MemberVO();

        memberVO.setId( member.getId() );
        memberVO.setNickName( member.getNickName() );
        memberVO.setEmail( member.getEmail() );
        memberVO.setUid( member.getUid() );
        memberVO.setAvatarUrl( member.getAvatarUrl() );
        memberVO.setMobile( member.getMobile() );
        memberVO.setAlipay( member.getAlipay() );
        memberVO.setWechat( member.getWechat() );
        memberVO.setChainAddress( member.getChainAddress() );
        memberVO.setStatus( member.getStatus() );
        memberVO.setAuthStatus( member.getAuthStatus() );
        memberVO.setExt( member.getExt() );
        memberVO.setSafeCode( member.getSafeCode() );
        memberVO.setDeleted( member.getDeleted() );
        memberVO.setCreated( fromLocalDateTime( member.getCreated() ) );

        return memberVO;
    }

    @Override
    public MemberDTO po2dto(UmsMember member) {
        if ( member == null ) {
            return null;
        }

        MemberDTO memberDTO = new MemberDTO();

        memberDTO.setId( member.getId() );
        memberDTO.setGender( member.getGender() );
        memberDTO.setNickName( member.getNickName() );
        memberDTO.setAlipayId( member.getAlipayId() );
        memberDTO.setAlipay( member.getAlipay() );
        memberDTO.setOpenid( member.getOpenid() );
        memberDTO.setWechat( member.getWechat() );
        memberDTO.setChainAddress( member.getChainAddress() );
        memberDTO.setMobile( member.getMobile() );
        memberDTO.setEmail( member.getEmail() );
        memberDTO.setAvatarUrl( member.getAvatarUrl() );
        memberDTO.setInviteCode( member.getInviteCode() );
        memberDTO.setAuthStatus( member.getAuthStatus() );
        memberDTO.setStatus( member.getStatus() );
        memberDTO.setDeleted( member.getDeleted() );
        JSONObject jSONObject = member.getExt();
        if ( jSONObject != null ) {
            memberDTO.setExt( new JSONObject( jSONObject ) );
        }

        return memberDTO;
    }

    @Override
    public MemberCoinRank po2rank(UmsMember member) {
        if ( member == null ) {
            return null;
        }

        MemberCoinRank memberCoinRank = new MemberCoinRank();

        memberCoinRank.setId( member.getId() );
        memberCoinRank.setNickName( member.getNickName() );
        memberCoinRank.setUid( member.getUid() );
        memberCoinRank.setAvatarUrl( member.getAvatarUrl() );
        memberCoinRank.setCreated( fromLocalDateTime( member.getCreated() ) );

        return memberCoinRank;
    }

    @Override
    public MemberSimpleVO po2SimpleVo(UmsMember member) {
        if ( member == null ) {
            return null;
        }

        MemberSimpleVO memberSimpleVO = new MemberSimpleVO();

        memberSimpleVO.setNickName( member.getNickName() );
        memberSimpleVO.setUid( member.getUid() );
        memberSimpleVO.setSafeCode( member.getSafeCode() );
        memberSimpleVO.setChainAddress( member.getChainAddress() );

        return memberSimpleVO;
    }

    @Override
    public MemberVO po2vo(com.muling.mall.ums.es.entity.UmsMember member) {
        if ( member == null ) {
            return null;
        }

        MemberVO memberVO = new MemberVO();

        memberVO.setId( member.getId() );
        memberVO.setNickName( member.getNickName() );
        memberVO.setEmail( member.getEmail() );
        memberVO.setUid( member.getUid() );
        memberVO.setAvatarUrl( member.getAvatarUrl() );
        memberVO.setMobile( member.getMobile() );
        memberVO.setChainAddress( member.getChainAddress() );
        memberVO.setStatus( member.getStatus() );
        memberVO.setAuthStatus( member.getAuthStatus() );
        memberVO.setExt( member.getExt() );
        memberVO.setDeleted( member.getDeleted() );
        memberVO.setCreated( fromLocalDateTime( member.getCreated() ) );

        return memberVO;
    }

    @Override
    public MemberSimpleDTO po2simpleDTO(UmsMember member) {
        if ( member == null ) {
            return null;
        }

        MemberSimpleDTO memberSimpleDTO = new MemberSimpleDTO();

        memberSimpleDTO.setId( member.getId() );
        memberSimpleDTO.setNickName( member.getNickName() );
        memberSimpleDTO.setAvatarUrl( member.getAvatarUrl() );
        memberSimpleDTO.setWechat( member.getWechat() );

        return memberSimpleDTO;
    }

    @Override
    public MemberAuthDTO po2authDTO(UmsMember member) {
        if ( member == null ) {
            return null;
        }

        MemberAuthDTO memberAuthDTO = new MemberAuthDTO();

        memberAuthDTO.setMemberId( member.getId() );
        memberAuthDTO.setUsername( member.getNickName() );
        memberAuthDTO.setAlipayId( member.getAlipayId() );
        memberAuthDTO.setStatus( member.getStatus() );
        memberAuthDTO.setPassword( member.getPassword() );
        memberAuthDTO.setSecret( member.getSecret() );
        memberAuthDTO.setSalt( member.getSalt() );
        memberAuthDTO.setIsBindGoogle( member.getIsBindGoogle() );

        return memberAuthDTO;
    }

    @Override
    public MemberRegisterDTO po2registerDTO(UmsMember member) {
        if ( member == null ) {
            return null;
        }

        MemberRegisterDTO memberRegisterDTO = new MemberRegisterDTO();

        memberRegisterDTO.setId( member.getId() );
        memberRegisterDTO.setNickName( member.getNickName() );
        memberRegisterDTO.setInviteCode( member.getInviteCode() );
        memberRegisterDTO.setMobile( member.getMobile() );

        return memberRegisterDTO;
    }

    @Override
    public UmsMember dto2Po(MemberDTO memberDTO) {
        if ( memberDTO == null ) {
            return null;
        }

        UmsMember umsMember = new UmsMember();

        umsMember.setId( memberDTO.getId() );
        umsMember.setGender( memberDTO.getGender() );
        umsMember.setNickName( memberDTO.getNickName() );
        umsMember.setEmail( memberDTO.getEmail() );
        umsMember.setMobile( memberDTO.getMobile() );
        umsMember.setAlipayId( memberDTO.getAlipayId() );
        umsMember.setAlipay( memberDTO.getAlipay() );
        umsMember.setOpenid( memberDTO.getOpenid() );
        umsMember.setWechat( memberDTO.getWechat() );
        umsMember.setAvatarUrl( memberDTO.getAvatarUrl() );
        umsMember.setStatus( memberDTO.getStatus() );
        umsMember.setAuthStatus( memberDTO.getAuthStatus() );
        umsMember.setChainAddress( memberDTO.getChainAddress() );
        umsMember.setInviteCode( memberDTO.getInviteCode() );
        JSONObject jSONObject = memberDTO.getExt();
        if ( jSONObject != null ) {
            umsMember.setExt( new JSONObject( jSONObject ) );
        }
        umsMember.setDeleted( memberDTO.getDeleted() );

        return umsMember;
    }
}

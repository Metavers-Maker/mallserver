package com.muling.mall.ums.converter;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.ums.pojo.entity.UmsMemberInvite;
import com.muling.mall.ums.pojo.form.MemberInviteForm;
import com.muling.mall.ums.pojo.vo.MemberInviteVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-06-14T23:25:17+0800",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 14.0.1 (Oracle Corporation)"
)
@Component
public class MemberInviteConverterImpl implements MemberInviteConverter {

    @Override
    public void updatePo(MemberInviteForm form, UmsMemberInvite memberInvite) {
        if ( form == null ) {
            return;
        }

        memberInvite.setMemberId( form.getMemberId() );
        memberInvite.setInviteMemberId( form.getInviteMemberId() );
        memberInvite.setAuthStatus( form.getAuthStatus() );
        memberInvite.setExt( form.getExt() );
    }

    @Override
    public MemberInviteVO po2vo(UmsMemberInvite memberInvite) {
        if ( memberInvite == null ) {
            return null;
        }

        MemberInviteVO memberInviteVO = new MemberInviteVO();

        memberInviteVO.setMemberId( memberInvite.getMemberId() );
        memberInviteVO.setInviteCode( memberInvite.getInviteCode() );
        memberInviteVO.setAuthStatus( memberInvite.getAuthStatus() );
        memberInviteVO.setExt( memberInvite.getExt() );
        memberInviteVO.setCreated( fromLocalDateTime( memberInvite.getCreated() ) );

        return memberInviteVO;
    }

    @Override
    public Page<MemberInviteVO> entity2PageVO(Page<UmsMemberInvite> entityPage) {
        if ( entityPage == null ) {
            return null;
        }

        Page<MemberInviteVO> page = new Page<MemberInviteVO>();

        page.setPages( entityPage.getPages() );
        page.setRecords( umsMemberInviteListToMemberInviteVOList( entityPage.getRecords() ) );
        page.setTotal( entityPage.getTotal() );
        page.setSize( entityPage.getSize() );
        page.setCurrent( entityPage.getCurrent() );
        page.setSearchCount( entityPage.isSearchCount() );
        page.setOptimizeCountSql( entityPage.isOptimizeCountSql() );
        List<OrderItem> list1 = entityPage.getOrders();
        if ( list1 != null ) {
            page.setOrders( new ArrayList<OrderItem>( list1 ) );
        }
        page.setCountId( entityPage.getCountId() );
        page.setMaxLimit( entityPage.getMaxLimit() );

        return page;
    }

    protected List<MemberInviteVO> umsMemberInviteListToMemberInviteVOList(List<UmsMemberInvite> list) {
        if ( list == null ) {
            return null;
        }

        List<MemberInviteVO> list1 = new ArrayList<MemberInviteVO>( list.size() );
        for ( UmsMemberInvite umsMemberInvite : list ) {
            list1.add( po2vo( umsMemberInvite ) );
        }

        return list1;
    }
}

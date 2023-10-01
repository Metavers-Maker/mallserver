package com.muling.mall.ums.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.ums.pojo.dto.RelationDTO;
import com.muling.mall.ums.pojo.entity.UmsRelation;
import com.muling.mall.ums.pojo.vo.MemberCoinRank;

import java.util.List;

public interface IUmsRelationService extends IService<UmsRelation> {

    public IPage<RelationDTO> follows(Page<RelationDTO> page, Long memberId);

    public IPage<RelationDTO> fans(Page<RelationDTO> page, Long memberId);

    public boolean follow(Long memberId, Long followId);

    public boolean unFollow(Long memberId, Long followId);

    public int getFollowCount(long memberId);

    public int getFansCount(long memberId);

    public List<MemberCoinRank> rankByCoin(Long pageNum,Long pageSize,Integer coinType);
}

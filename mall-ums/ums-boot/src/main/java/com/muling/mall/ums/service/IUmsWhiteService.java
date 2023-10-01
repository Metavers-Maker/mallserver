package com.muling.mall.ums.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.ums.pojo.dto.RelationDTO;
import com.muling.mall.ums.pojo.entity.UmsRelation;
import com.muling.mall.ums.pojo.entity.UmsWhite;
import com.muling.mall.ums.pojo.form.MemberWhiteForm;

public interface IUmsWhiteService extends IService<UmsWhite> {
    public boolean create(Long memberId,MemberWhiteForm whiteForm);
    public boolean updateLevel(Integer level,String mobile);
    public Integer whiteLevel(String mobile);

}

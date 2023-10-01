package com.muling.mall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.pms.pojo.entity.PmsHot;
import com.muling.mall.pms.pojo.form.HotForm;
import com.muling.mall.pms.pojo.vo.HotVO;

import java.util.List;

public interface IPmsHotService extends IService<PmsHot> {

    public List<HotVO> getLists();

    public boolean save(HotForm hotForm);

    public boolean updateById(Long id, HotForm hotForm);
}

package com.muling.mall.bms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.exception.BizException;
import com.muling.common.redis.utils.BusinessNoGenerator;
import com.muling.common.result.ResultCode;
import com.muling.mall.bms.converter.AirdropItemConverter;
import com.muling.mall.bms.mapper.BmsAirdropItemMapper;
import com.muling.mall.bms.pojo.entity.BmsAirdropItem;
import com.muling.mall.bms.pojo.form.AirdropItemForm;
import com.muling.mall.bms.pojo.query.AirdropItemPageQuery;
import com.muling.mall.bms.pojo.vo.AirdropItemVO;
import com.muling.mall.bms.service.IBmsAirdropItemService;
import com.muling.mall.bms.service.IMemberItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BmsAirdropItemServiceImpl extends ServiceImpl<BmsAirdropItemMapper, BmsAirdropItem> implements IBmsAirdropItemService {

    private final BusinessNoGenerator businessNoGenerator;

    private final IMemberItemService memberItemService;

    @Override
    public IPage<AirdropItemVO> page(AirdropItemPageQuery queryParams) {
        LambdaQueryWrapper<BmsAirdropItem> queryWrapper = new LambdaQueryWrapper<BmsAirdropItem>()
                .orderByDesc(BmsAirdropItem::getUpdated);
        Page<BmsAirdropItem> page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);
        Page<AirdropItemVO> result = AirdropItemConverter.INSTANCE.entity2PageVO(page);
        return result;
    }

    @Override
    public boolean save(AirdropItemForm airdropItemForm) {
        BmsAirdropItem airdropItem = AirdropItemConverter.INSTANCE.form2Po(airdropItemForm);
        boolean b = this.save(airdropItem);
        if (!b) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        return b;
    }

    @Override
    public boolean updateById(Long id, AirdropItemForm airdropItemForm) {
        BmsAirdropItem airdropItem = getById(id);
        if (airdropItem == null) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        BeanUtil.copyProperties(airdropItemForm, airdropItem);
        return updateById(airdropItem);
    }

    @Override
    public boolean exec(Long cfgId) {
        //
        try {
            Integer pageNum = 1;
            Integer pageSize = 100;
            LambdaQueryWrapper<BmsAirdropItem> queryWrapper = new LambdaQueryWrapper<BmsAirdropItem>()
                    .eq(BmsAirdropItem::getAirdropId, cfgId)
                    .eq(BmsAirdropItem::getStatus, 0)
                    .orderByDesc(BmsAirdropItem::getCreated);
            while (true) {
                Page<BmsAirdropItem> page = this.baseMapper.selectPage(new Page(pageNum, pageSize), queryWrapper);
                if (page.getRecords().isEmpty()) {
                    break;
                }
                //执行空投
                page.getRecords().forEach(item -> {
                    boolean ret = memberItemService.airdrop(item.getMemberId(), item.getSpuId(), item.getSpuCount(), "空投活动：" + cfgId);
                    LambdaUpdateWrapper<BmsAirdropItem> updateWrapper = new LambdaUpdateWrapper<BmsAirdropItem>();
                    updateWrapper.eq(BmsAirdropItem::getId, item.getId());
                    if (ret) {
                        //成功
                        updateWrapper.set(BmsAirdropItem::getStatus, 1);
                    } else {
                        //失败
                        updateWrapper.set(BmsAirdropItem::getStatus, 2);
                    }
                    this.update(updateWrapper);
                });
            }
        } catch (Exception e) {
//            logger.error("", e);
            throw e;
        } finally {

        }
        return true;
    }
}

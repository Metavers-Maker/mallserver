package com.muling.mall.farm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.common.util.DateUtils;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.bms.dto.MemberItemDTO;
import com.muling.mall.farm.enums.FarmOpEnum;
import com.muling.mall.farm.mapper.FarmMemberMapper;
import com.muling.mall.farm.pojo.entity.FarmBagConfig;
import com.muling.mall.farm.pojo.entity.FarmConfig;
import com.muling.mall.farm.pojo.entity.FarmMember;
import com.muling.mall.farm.pojo.entity.FarmMemberItem;
import com.muling.mall.farm.service.*;
import jnr.ffi.annotations.In;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FarmMemberServiceImpl extends ServiceImpl<FarmMemberMapper, FarmMember> implements IFarmMemberService {

    private final IFarmMemberItemService farmMemberItemService;

    private final IFarmMemberLogService farmMemberLogService;

    private final IFarmAdService farmAdService;

    public FarmMember getByMemberId(Long memberId, Long farmId) {
        return this.baseMapper.getByMemberId(memberId, farmId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(MemberItemDTO memberItemDTO, FarmConfig farmConfig, FarmBagConfig farmBagConfig) {
        boolean exists = baseMapper.exists(Wrappers.<FarmMember>lambdaQuery()
                .eq(FarmMember::getMemberId, memberItemDTO.getMemberId()));
        //计算每天可以领多少奖励
        BigDecimal claimValue = farmBagConfig.getClaimCoinValue().divide(BigDecimal.valueOf(farmBagConfig.getMinDays()), RoundingMode.HALF_EVEN);
        BigDecimal rakeBackValue = farmBagConfig.getRakeBackCoinValue().divide(BigDecimal.valueOf(farmBagConfig.getMinDays()), RoundingMode.HALF_EVEN);
        BigDecimal activeValue = farmBagConfig.getActiveValueExt().divide(BigDecimal.valueOf(farmBagConfig.getMinDays()), RoundingMode.HALF_EVEN);
        FarmMember farmMember = null;
        boolean f = false;
        if (!exists) {
            farmMember = new FarmMember();
            farmMember.setFarmId(farmConfig.getId());
            farmMember.setMemberId(memberItemDTO.getMemberId());
            farmMember.setClaimCoinType(farmConfig.getClaimCoinType());
            farmMember.setClaimCoinValue(claimValue);
            farmMember.setRakeBackCoinType(farmConfig.getRakeBackCoinType());
            farmMember.setRakeBackCoinValue(rakeBackValue);
            farmMember.setActiveValueExt(activeValue);
            farmMember.setClaimedActiveValueExt(BigDecimal.ZERO);
            f = save(farmMember);
        } else {
            farmMember = this.baseMapper.getByMemberId(memberItemDTO.getMemberId(), farmConfig.getId());
            BigDecimal claimCoinValue = farmMember.getClaimCoinValue();
            farmMember.setClaimCoinValue(claimCoinValue.add(claimValue));

            BigDecimal rakeBackCoinValue = farmMember.getRakeBackCoinValue();
            farmMember.setRakeBackCoinValue(rakeBackCoinValue.add(rakeBackValue));

            BigDecimal activeValueExt = farmMember.getActiveValueExt();
            farmMember.setActiveValueExt(activeValueExt.add(activeValue));

            f = updateById(farmMember);
        }
        //创建工作包
        exists = farmMemberItemService.create(memberItemDTO, farmMember, farmConfig, farmBagConfig);
        if (f) {
            //重置工作包
            this.reset(farmMember.getMemberId(),farmMember.getFarmId());
        }
        return exists;
    }

    //开启
    @Transactional(rollbackFor = Exception.class)
    public boolean open(FarmMember farmMember, FarmConfig farmConfig) {
        if(farmAdService.isComplete(farmConfig.getMaxNum()) == false) {
            //Ad任务未完成开启工作包，需要重新设置时间
            farmMember.setAllowClaimed(LocalDateTime.now().plusSeconds(farmConfig.getLimitHour()));
        } else {
            //Ad任务完成开启工作包，马上就可以领取
            farmMember.setAllowClaimed(LocalDateTime.now());
        }
        farmMember.setStatus(0);
        //开启Farm的时候做数据修正
        //获取所有的工作包
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<FarmMemberItem> wrapper = Wrappers.<FarmMemberItem>lambdaQuery()
                .eq(FarmMemberItem::getMemberId, farmMember.getMemberId())
                .ge(FarmMemberItem::getClosed, now)
                .eq(FarmMemberItem::getStatus,0)
                .orderByDesc(FarmMemberItem::getCreated);
        Page<FarmMemberItem> page = farmMemberItemService.page(new Page(1, 100), wrapper);
        if (!page.getRecords().isEmpty()) {
            //收获的积分
            BigDecimal claimCoinTotal = new BigDecimal(0.0);
            //返佣的积分
            BigDecimal rebakeCoinTotal = new BigDecimal(0.0);
            //按周期获取的活跃度
            BigDecimal activeValueExTotal = new BigDecimal(0.0);
            //设置各种积分
            for (FarmMemberItem memberItem : page.getRecords()) {
                claimCoinTotal = claimCoinTotal.add(memberItem.getClaimCoinValue());
                rebakeCoinTotal = rebakeCoinTotal.add(memberItem.getRakeBackCoinValue());
                activeValueExTotal = activeValueExTotal.add(memberItem.getActiveValueExt());
            }
            farmMember.setClaimCoinValue(claimCoinTotal);
            farmMember.setRakeBackCoinValue(rebakeCoinTotal);
            farmMember.setActiveValueExt(activeValueExTotal);
        } else {
            //没有有效工作包，则为0
            farmMember.setClaimCoinValue(BigDecimal.ZERO);
            farmMember.setRakeBackCoinValue(BigDecimal.ZERO);
            farmMember.setActiveValueExt(BigDecimal.ZERO);
        }
        //更新数据
        boolean b = updateById(farmMember);
        if (b) {
            farmMemberLogService.save(farmMember, FarmOpEnum.OPEN);
        }
        return b;
    }

    //领奖励
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean claim(FarmMember farmMember,Integer parentBurnCode) {
        farmMember.setClaimed(LocalDateTime.now());
        //按周期返的活跃度
        BigDecimal claimActiveValueExt = farmMember.getActiveValueExt().add(farmMember.getClaimedActiveValueExt());
        farmMember.setClaimedActiveValueExt(claimActiveValueExt);
        //
        boolean result = this.updateById(farmMember);
        if (result) {
            result = farmMemberItemService.claim(farmMember,parentBurnCode);
            farmMemberLogService.save(farmMember, FarmOpEnum.CLAIM);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean close(List<FarmMemberItem> farmMemberItems) {
        //批量关闭工作包
        farmMemberItems.stream().forEach(farmMemberItem -> {
            farmMemberItemService.close(farmMemberItem);
            //每处理一个工作包，都需要重置用户表信息
            reset(farmMemberItem.getMemberId(),farmMemberItem.getFarmId());
        });
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean claimPass(Long farmId) {
        //强制用户可以获取
        Long memberId = MemberUtils.getMemberId();
        FarmMember farmMember = this.getByMemberId(memberId, farmId);
        if (farmMember == null) {
            throw new BizException(ResultCode.REQUEST_INVALID, "未发现Farmer");
        }
        farmMember.setAllowClaimed(LocalDateTime.now());
        return true;
    }

    //工作包激活或者解冻
    public boolean activate(FarmMemberItem farmMemberItem, FarmBagConfig farmBagConfig) {
        FarmMember farmMember = this.getByMemberId(farmMemberItem.getMemberId(), farmMemberItem.getFarmId());
        if (farmMember == null) {
            throw new BizException(ResultCode.REQUEST_INVALID, "未发现Farmer");
        }
        boolean activate = farmMemberItemService.activate(farmMember,farmMemberItem, farmBagConfig);
        if (activate) {
            //工作包激活,重新计算奖励
            this.reset(farmMemberItem.getMemberId(),farmMemberItem.getFarmId());
        }
        return activate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reset(Long memberId,Long farmId) {
        FarmMember farmMember = this.getByMemberId(memberId, farmId);
        if (farmMember == null) {
            return false;
        }
        //获取用户所有开启的工作包
        LambdaQueryWrapper<FarmMemberItem> wrapper = Wrappers.<FarmMemberItem>lambdaQuery()
                .eq(FarmMemberItem::getMemberId, memberId)
                .eq(FarmMemberItem::getStatus,0)
                .orderByDesc(FarmMemberItem::getCreated);
        Page<FarmMemberItem> page = farmMemberItemService.page(new Page(1, 100), wrapper);
        //计算奖励
        BigDecimal claimCoinTotal = new BigDecimal(0.0);
        BigDecimal rebakeCoinTotal = new BigDecimal(0.0);
        BigDecimal activeValueExTotal = new BigDecimal(0.0);
        Integer burnCode = 0;
        if (!page.getRecords().isEmpty()) {
            for (FarmMemberItem memberItem : page.getRecords()) {
                claimCoinTotal = claimCoinTotal.add(memberItem.getClaimCoinValue());
                rebakeCoinTotal = rebakeCoinTotal.add(memberItem.getRakeBackCoinValue());
                activeValueExTotal = activeValueExTotal.add(memberItem.getActiveValueExt());
                //
                if( memberItem.getName().indexOf("实习")!=-1 ) {
                    burnCode = burnCode | 0x0001;
                } else if( memberItem.getName().indexOf("初级")!=-1 ) {
                    burnCode = burnCode | 0x0002;
                } else if( memberItem.getName().indexOf("中级")!=-1 ) {
                    burnCode = burnCode | 0x0004;
                } else if( memberItem.getName().indexOf("高级")!=-1 ) {
                    burnCode = burnCode | 0x0008;
                } else if( memberItem.getName().indexOf("专家")!=-1 ) {
                    burnCode = burnCode | 0x0010;
                } else if( memberItem.getName().indexOf("资深")!=-1 ) {
                    burnCode = burnCode | 0x0020;
                } else if( memberItem.getName().indexOf("体验")!=-1 ) {
                    burnCode = burnCode | 0x0040;
                }
            }
        }
        //更新数据
        farmMember.setBurnCode(burnCode);
        farmMember.setClaimCoinValue(claimCoinTotal);
        farmMember.setRakeBackCoinValue(rebakeCoinTotal);
        farmMember.setActiveValueExt(activeValueExTotal);
        boolean f = this.updateById(farmMember);
        return f;
    }
}

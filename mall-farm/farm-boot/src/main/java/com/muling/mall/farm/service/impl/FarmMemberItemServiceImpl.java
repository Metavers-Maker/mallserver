package com.muling.mall.farm.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.muling.common.exception.BizException;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.util.DateUtils;
import com.muling.mall.bms.dto.MemberItemDTO;
import com.muling.mall.farm.converter.FarmMemberItemConverter;
import com.muling.mall.farm.enums.FarmOpEnum;
import com.muling.mall.farm.mapper.FarmMemberItemMapper;
import com.muling.mall.farm.pojo.entity.FarmBagConfig;
import com.muling.mall.farm.pojo.entity.FarmConfig;
import com.muling.mall.farm.pojo.entity.FarmMember;
import com.muling.mall.farm.pojo.entity.FarmMemberItem;
import com.muling.mall.farm.service.IFarmBagConfigService;
import com.muling.mall.farm.service.IFarmMemberItemService;
import com.muling.mall.farm.service.IFarmMemberLogService;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.api.MemberInviteFeignClient;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import com.muling.mall.ums.pojo.dto.MemberInviteDTO;
import com.muling.mall.wms.api.WalletFeignClient;
import com.muling.mall.wms.enums.WalletOpTypeEnum;
import com.muling.mall.wms.pojo.dto.WalletDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FarmMemberItemServiceImpl extends ServiceImpl<FarmMemberItemMapper, FarmMemberItem> implements IFarmMemberItemService {

    private final IFarmMemberLogService farmMemberLogService;

    private final IFarmBagConfigService farmBagConfigService;

    private final WalletFeignClient walletFeignClient;

    private final MemberInviteFeignClient memberInviteFeignClient;

    //创建工作包
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(MemberItemDTO memberItemDTO, FarmMember farmMember, FarmConfig farmConfig, FarmBagConfig farmBagConfig) {
        boolean exists = baseMapper.exists(Wrappers.<FarmMemberItem>lambdaQuery()
                .eq(FarmMemberItem::getItemId, memberItemDTO.getId()));
        if (exists) {
            return false;
        }
        FarmMemberItem farmMemberItem = FarmMemberItemConverter.INSTANCE.dto2po(memberItemDTO);
        //计算收获奖励
        BigDecimal claimValue = farmBagConfig.getClaimCoinValue().divide(BigDecimal.valueOf(farmBagConfig.getMinDays()), RoundingMode.HALF_EVEN);
        //计算返利奖励
        BigDecimal rakeBackValue = farmBagConfig.getRakeBackCoinValue().divide(BigDecimal.valueOf(farmBagConfig.getMinDays()), RoundingMode.HALF_EVEN);
        //计算按周期返活跃度
        BigDecimal activeValueExt = farmBagConfig.getActiveValueExt().divide(BigDecimal.valueOf(farmBagConfig.getMinDays()), RoundingMode.HALF_EVEN);
        //记录一次返佣活跃度
        farmMemberItem.setRakeActiveOnce(farmBagConfig.getRakeBackActiveValue());
        //记录按周期返活跃度
        farmMemberItem.setActiveValueExt(activeValueExt);
        //记录一共领取的活跃度（初始化）
        farmMemberItem.setClaimedActiveValueExt(BigDecimal.ZERO);
        //记录获取奖励
        farmMemberItem.setClaimCoinType(farmConfig.getClaimCoinType());
        farmMemberItem.setClaimCoinValue(claimValue);
        //记录返佣奖励
        farmMemberItem.setRakeBackCoinType(farmConfig.getRakeBackCoinType());
        farmMemberItem.setRakeBackCoinValue(rakeBackValue);
        //计算时间
        LocalDateTime now = LocalDateTime.now();
        Date nowDate = DateUtils.localDateTimeToDate(now);
        Date claimed = DateUtils.localDateTimeToDate(farmMember.getClaimed());
        Integer minDays = farmBagConfig.getMinDays();
        //如果今天领过奖励，延迟一天关闭，防止少领奖励
        if (claimed != null && DateUtil.isSameDay(nowDate, claimed)) {
            minDays = minDays + 1;
        }
        LocalDateTime closed = now.plusDays(minDays);
        LocalDateTime endClosed = LocalDateTime.of(closed.toLocalDate(), LocalTime.MAX);
        //记录关闭时间
        farmMemberItem.setClosed(endClosed);
        farmMemberItem.setFarmId(farmConfig.getId());
        //设置执行次数为0
        farmMemberItem.setExecTimes(0);
        boolean save = this.save(farmMemberItem);
        farmMemberLogService.save(farmMemberItem, FarmOpEnum.CREATE);
        log.info("创建工作包:{}.{}", JSONUtil.toJsonStr(farmMember), JSONUtil.toJsonStr(farmMemberItem));
        return save;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean activate(FarmMember farmMember, FarmMemberItem farmMemberItem, FarmBagConfig config) {
        //
        Assert.isTrue(farmMemberItem.getStatus() == 1, "工作包未到期，不能激活");
        //激活工作包
        Integer currPeriod = farmMemberItem.getCurrPeriod();
        Integer period = config.getPeriod();
        Integer nextPeriod = currPeriod + 1;
        if (farmMemberItem.getStatus().compareTo(3) == 0) {
            //解冻周期不变
            nextPeriod = currPeriod;
        }
        if (period < nextPeriod) {
            return false;
        }
        //计算关闭时间
        int days = Math.multiplyExact(nextPeriod, config.getStep());
        int totalDays = Math.addExact(config.getMinDays(), days);
        if (totalDays > config.getMaxDays()) {
            totalDays = config.getMaxDays();
        }
        //判断今天是否完成奖励
        int addOneDay = 0;
        LocalDateTime now = LocalDateTime.now();
        Date nowDate = DateUtils.localDateTimeToDate(now);
        Date claimed = DateUtils.localDateTimeToDate(farmMember.getClaimed());
        //如果今天领过奖励，延迟一天关闭，防止少领奖励
        if (claimed != null && DateUtil.isSameDay(nowDate, claimed)) {
            addOneDay = 1;
        }
        //设置关闭时间
        LocalDateTime closed = LocalDateTime.now().plusDays(totalDays + addOneDay);
        LocalDateTime endClosed = LocalDateTime.of(closed.toLocalDate(), LocalTime.MAX);
        farmMemberItem.setClosed(endClosed);
        farmMemberItem.setCurrPeriod(nextPeriod);
        farmMemberItem.setStatus(0);    //0表示激活，1表示关闭，2表示扣除
        //计算目标工作包相关数据
        //记入返佣活跃度
        farmMemberItem.setRakeActiveOnce(config.getRakeBackActiveValue());
        ;
        //重新计算奖励（每日）
        BigDecimal claimValue = config.getClaimCoinValue().divide(BigDecimal.valueOf(totalDays), RoundingMode.HALF_EVEN);
        farmMemberItem.setClaimCoinValue(claimValue);
        //重新计算返佣（每日）
        BigDecimal rakeBackCoinValue = config.getRakeBackCoinValue().divide(BigDecimal.valueOf(totalDays), RoundingMode.HALF_EVEN);
        farmMemberItem.setRakeBackCoinValue(rakeBackCoinValue);
        //重新计算活跃度（每日）
        BigDecimal activeValueExt = config.getActiveValueExt().divide(BigDecimal.valueOf(totalDays), RoundingMode.HALF_EVEN);
        farmMemberItem.setActiveValueExt(activeValueExt);
        //初始化（一共领取多少活跃度）
        farmMemberItem.setClaimedActiveValueExt(BigDecimal.ZERO);
        //设置执行次数为0
        farmMemberItem.setExecTimes(0);
        //更新数据
        boolean result = updateById(farmMemberItem);
        if (result) {
            if (farmMemberItem.getStatus().compareTo(3) == 0) {
                //解冻
                farmMemberLogService.save(farmMemberItem, FarmOpEnum.UNFREEZE);
            } else {
                //正常激活逻辑
                farmMemberLogService.save(farmMemberItem, FarmOpEnum.ACTIVATE);
            }
        }
        return result;
    }

    //关闭工作包（正常）
    @Transactional(rollbackFor = Exception.class)
    public boolean close(FarmMemberItem farmMemberItem) {
        //设置状态为1(0开启，1关闭)
        farmMemberItem.setStatus(1);
        //设置一共领取的活跃度为0
        farmMemberItem.setClaimedActiveValueExt(BigDecimal.ZERO);
        boolean result = updateById(farmMemberItem);
        if (result) {
            //需要扣除相关的活跃度
            farmMemberLogService.save(farmMemberItem, FarmOpEnum.CLOSE);
        }
        return result;
    }

    @Override
    public boolean claim(FarmMember farmMember,Integer parentBurnCode) {
        //
        List<FarmMemberItem> openLists = list(Wrappers.<FarmMemberItem>lambdaQuery()
                .eq(FarmMemberItem::getFarmId, farmMember.getFarmId())
                .eq(FarmMemberItem::getMemberId, farmMember.getMemberId())
                .eq(FarmMemberItem::getStatus, 0)
        );
        BigDecimal rakeValue = BigDecimal.ZERO;
        for (FarmMemberItem farmMemberItem : openLists) {
            //获得按周期领取的活跃度
            BigDecimal activeValueExt = farmMemberItem.getActiveValueExt();
            //记录一共领取了多少活跃度
            farmMemberItem.setClaimedActiveValueExt(activeValueExt.add(farmMemberItem.getClaimedActiveValueExt()));
            //根据烧伤计算返佣
            Integer targetCode = 0;
            if( farmMemberItem.getName().indexOf("实习")!=-1 ) {
                targetCode = 0x0001;
            } else if( farmMemberItem.getName().indexOf("初级")!=-1 ) {
                targetCode = 0x0002;
            } else if( farmMemberItem.getName().indexOf("中级")!=-1 ) {
                targetCode = 0x0004;
            } else if( farmMemberItem.getName().indexOf("高级")!=-1 ) {
                targetCode = 0x0008;
            } else if( farmMemberItem.getName().indexOf("专家")!=-1 ) {
                targetCode = 0x0010;
            } else if( farmMemberItem.getName().indexOf("资深")!=-1 ) {
                targetCode = 0x0020;
            } else if( farmMemberItem.getName().indexOf("体验")!=-1 ) {
                targetCode = 0x0040;
            }
            //
            Integer combineCode = parentBurnCode & targetCode;
            if (!combineCode.equals(0)) {
                rakeValue = rakeValue.add(farmMemberItem.getRakeBackCoinValue());
            }
        }
        //设置返佣数值
        farmMember.setRakeBackCoinValue(rakeValue);
        return updateBatchById(openLists);
    }

    @Override
    public FarmMemberItem getByItemId(Long itemId) {
        return this.baseMapper.getByItemId(itemId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refresh(Long id) {
        FarmMemberItem farmMemberItem = this.getById(id);
        if (farmMemberItem == null) {
            return false;
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean disableByAdmin(Long id) {
        LambdaUpdateWrapper<FarmMemberItem> lambdaUpdateWrapper = new LambdaUpdateWrapper<FarmMemberItem>();
        lambdaUpdateWrapper.eq(FarmMemberItem::getId, id);
        lambdaUpdateWrapper.set(FarmMemberItem::getStatus, 2);   //2表示封禁
        boolean f = this.update(lambdaUpdateWrapper);
        if (f) {
            //重新计算用户
        }
        return f;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enableByAdmin(Long id) {
        LambdaUpdateWrapper<FarmMemberItem> lambdaUpdateWrapper = new LambdaUpdateWrapper<FarmMemberItem>();
        lambdaUpdateWrapper.eq(FarmMemberItem::getId, id);
        lambdaUpdateWrapper.set(FarmMemberItem::getStatus, 0);   //0表示激活
        boolean f = this.update(lambdaUpdateWrapper);
        if (f) {
            //
        }
        return f;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closeByAdmin(Long id) {
        LambdaUpdateWrapper<FarmMemberItem> lambdaUpdateWrapper = new LambdaUpdateWrapper<FarmMemberItem>();
        lambdaUpdateWrapper.eq(FarmMemberItem::getId, id);
        lambdaUpdateWrapper.set(FarmMemberItem::getStatus, 1);   //1表示关闭
        boolean f = this.update(lambdaUpdateWrapper);
        if (f) {
            //
        }
        return f;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean freezeByAdmin(Long id) {
        LambdaUpdateWrapper<FarmMemberItem> lambdaUpdateWrapper = new LambdaUpdateWrapper<FarmMemberItem>();
        lambdaUpdateWrapper.eq(FarmMemberItem::getId, id);
        lambdaUpdateWrapper.set(FarmMemberItem::getStatus, 3);   //3表示冻结
        boolean f = this.update(lambdaUpdateWrapper);
        if (f) {
//            farmMemberLogService.save(farmMemberItem, FarmOpEnum.CLOSE);
        }
        return f;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean fixByAdmin(Long id) {
        LambdaQueryWrapper<FarmMemberItem> lambdaQueryWrapper = new LambdaQueryWrapper<FarmMemberItem>();
        lambdaQueryWrapper.eq(FarmMemberItem::getId, id);
        FarmMemberItem farmMemberItem = this.baseMapper.selectOne(lambdaQueryWrapper);
        if (farmMemberItem != null) {
            //上级用户
            Result<MemberInviteDTO> memberInvite = memberInviteFeignClient.getMemberInviteById(farmMemberItem.getMemberId());
            Assert.isTrue(memberInvite.getData() != null, "目标用户不存在");
            Long inviteMemberId = memberInvite.getData().getInviteMemberId();
            //获取目标配置
            FarmBagConfig farmBagConfig = farmBagConfigService.getBySpuId(farmMemberItem.getSpuId());
            Assert.isTrue(farmBagConfig != null, "工作包配置不存在");
            //重新计算工作包
            Integer totalDay = 30 + farmMemberItem.getCurrPeriod() * 2;
            LocalDateTime closeTime = farmMemberItem.getClosed();
            LocalDateTime openTime = closeTime.minusDays(totalDay);
            Duration duration = Duration.between(openTime, LocalDateTime.now());
            long dertTime = duration.toDays();
            BigDecimal oldRake = farmMemberItem.getRakeBackCoinValue();
            BigDecimal newRake = farmBagConfig.getRakeBackCoinValue().divide(BigDecimal.valueOf(totalDay), RoundingMode.HALF_EVEN);
            newRake = newRake.setScale(4);
            log.info("返佣修复 memberId:{},inviteId:{},dert:{},old:{},new:{}", farmMemberItem.getMemberId(), inviteMemberId, dertTime, oldRake, newRake);
            Assert.isTrue(oldRake.compareTo(newRake) != 0, "数据一致，不需修正");
            //
            farmMemberItem.setRakeBackCoinValue(newRake);
            //
            oldRake = oldRake.multiply(BigDecimal.valueOf(dertTime));
            newRake = newRake.multiply(BigDecimal.valueOf(dertTime));
            //
            List<WalletDTO> walletDTOList = Lists.newArrayList();
            //扣除返佣数据
            WalletDTO walletDTO0 = new WalletDTO();
            walletDTO0.setMemberId(inviteMemberId);
            walletDTO0.setCoinType(0);
            walletDTO0.setBalance(oldRake.negate());
            walletDTO0.setOpType(WalletOpTypeEnum.SYS_RAKE_MODIFY.getValue());
            walletDTO0.setRemark(WalletOpTypeEnum.SYS_RAKE_MODIFY.getLabel());
            walletDTOList.add(walletDTO0);
            WalletDTO walletDTO1 = new WalletDTO();
            walletDTO1.setMemberId(inviteMemberId);
            walletDTO1.setCoinType(1);
            walletDTO1.setBalance(oldRake.negate());
            walletDTO1.setOpType(WalletOpTypeEnum.SYS_RAKE_MODIFY.getValue());
            walletDTO1.setRemark(WalletOpTypeEnum.SYS_RAKE_MODIFY.getLabel());
            walletDTOList.add(walletDTO1);
            //增加返佣数据
            WalletDTO walletDTO3 = new WalletDTO();
            walletDTO3.setMemberId(inviteMemberId);
            walletDTO3.setCoinType(0);
            walletDTO3.setBalance(newRake);
            walletDTO3.setOpType(WalletOpTypeEnum.SYS_RAKE_MODIFY.getValue());
            walletDTO3.setRemark(WalletOpTypeEnum.SYS_RAKE_MODIFY.getLabel());
            walletDTOList.add(walletDTO3);
            WalletDTO walletDTO4 = new WalletDTO();
            walletDTO4.setMemberId(inviteMemberId);
            walletDTO4.setCoinType(1);
            walletDTO4.setBalance(newRake);
            walletDTO4.setOpType(WalletOpTypeEnum.SYS_RAKE_MODIFY.getValue());
            walletDTO4.setRemark(WalletOpTypeEnum.SYS_RAKE_MODIFY.getLabel());
            walletDTOList.add(walletDTO4);
            if (!walletDTOList.isEmpty()) {
                walletFeignClient.updateBalances(walletDTOList);
            }
            //
            boolean f = this.saveOrUpdate(farmMemberItem);
            if(f) {
                //
            }
            return f;
        }
        return false;
    }

}

package com.muling.mall.bms.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.bms.constant.OmsConstants;
import com.muling.mall.bms.enums.StatusEnum;
import com.muling.mall.bms.enums.ViewTypeEnum;
import com.muling.mall.bms.pojo.entity.BmsCoinConfig;
import com.muling.mall.bms.pojo.entity.OmsExchangeConfig;
import com.muling.mall.bms.pojo.form.admin.CoinRewardConfigForm;
import com.muling.mall.bms.pojo.form.admin.ExchangeConfigForm;
import com.muling.mall.bms.service.ICoinRewardConfigService;
import com.muling.mall.bms.service.IExchangeConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Api(tags = "admin-积分奖励配置")
@RestController("CoinRewardConfigController")
@RequestMapping("/api/v1/coin-reward-config")
@RequiredArgsConstructor
public class CoinRewardConfigController {
    private final ICoinRewardConfigService coinRewardConfigService;
    private final StringRedisTemplate redisTemplate;

    private final RabbitTemplate rabbitTemplate;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize
    ) {
        LambdaQueryWrapper<BmsCoinConfig> queryWrapper = new LambdaQueryWrapper<BmsCoinConfig>()
//                .like(StrUtil.isNotBlank(remark), OmsExchangeConfig::getRemark, remark)
                .orderByDesc(BmsCoinConfig::getUpdated)
                .orderByDesc(BmsCoinConfig::getCreated);
        Page<BmsCoinConfig> result = coinRewardConfigService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public Result getById(@ApiParam("id") @PathVariable Integer id) {
        BmsCoinConfig config = coinRewardConfigService.getById(id);
        return Result.success(config);
    }

    @ApiOperation(value = "新增")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(
            @RequestBody CoinRewardConfigForm configForm) {
        boolean status = coinRewardConfigService.save(configForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result updateById(
            @ApiParam("id") @PathVariable Long id,
            @RequestBody CoinRewardConfigForm configForm) {
        boolean status = coinRewardConfigService.updateById(id, configForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "显示")
    @PutMapping("/display")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result display(
            @ApiParam("ids") @RequestParam(required = true) List<Long> ids) {
        boolean status = coinRewardConfigService.update(new LambdaUpdateWrapper<BmsCoinConfig>()
                .in(BmsCoinConfig::getId, ids)
                .eq(BmsCoinConfig::getVisible, ViewTypeEnum.INVISIBLE)
                .set(BmsCoinConfig::getVisible, ViewTypeEnum.VISIBLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "隐藏")
    @PutMapping("/hide")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result hide(
            @ApiParam("ids") @RequestParam(required = true) List<Long> ids) {
        boolean status = coinRewardConfigService.update(new LambdaUpdateWrapper<BmsCoinConfig>()
                .in(BmsCoinConfig::getId, ids)
                .eq(BmsCoinConfig::getVisible, ViewTypeEnum.VISIBLE)
                .set(BmsCoinConfig::getVisible, ViewTypeEnum.INVISIBLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(
            @ApiParam("id集合") @PathVariable("ids") String ids) {
        boolean status = coinRewardConfigService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.judge(status);
    }

//    @ApiOperation(value = "[测试使用]-分发多模块消息")
//    @GetMapping("/dispatch")
//    public Result dispatch(
//            @ApiParam("用户物品ID") @RequestParam Long itemId) {
//
//        rabbitTemplate.convertAndSend("fanout.item.exchange", "", itemId);
//        return Result.success();
//    }
}

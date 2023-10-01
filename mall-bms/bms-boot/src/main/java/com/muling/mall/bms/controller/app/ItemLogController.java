
package com.muling.mall.bms.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.base.IBaseEnum;
import com.muling.common.result.PageResult;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.bms.enums.ItemLogTypeEnum;
import com.muling.mall.bms.pojo.entity.OmsItemLog;
import com.muling.mall.bms.service.IItemLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "app-物品日志管理")
@RestController
@RequestMapping("/app-api/v1/items/logs")
@RequiredArgsConstructor
public class ItemLogController {


    private final IItemLogService itemLogService;

    @ApiOperation("分页列表")
    @GetMapping
    public PageResult page(@ApiParam(value = "页码", example = "1") Long pageNum,
                           @ApiParam(value = "每页数量", example = "10") Long pageSize,
                           @ApiParam(value = "类型") Integer type) {

        Long memberId = MemberUtils.getMemberId();
        LambdaQueryWrapper<OmsItemLog> wrapper = Wrappers.<OmsItemLog>lambdaQuery()
                .eq(memberId != null, OmsItemLog::getMemberId, memberId);
        if (type != null) {
            wrapper.eq(type != null, OmsItemLog::getType, IBaseEnum.getEnumByValue(type, ItemLogTypeEnum.class));
        }
        wrapper.orderByDesc(OmsItemLog::getCreated);
        IPage<OmsItemLog> result = itemLogService.page(new Page(pageNum, pageSize), wrapper);
        return PageResult.success(result);
    }

}

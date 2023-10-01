
package com.muling.mall.bms.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.exception.BizException;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.mall.bms.es.service.IOmsMemberItemEService;
import com.muling.mall.bms.pojo.query.app.ItemPageQuery;
import com.muling.mall.bms.pojo.vo.app.MemberItemVO;
import com.muling.mall.bms.pojo.vo.app.OpenItemVO;
import com.muling.mall.bms.service.IMemberItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "app-会员物品管理")
@RestController
@RequestMapping("/app-api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final IMemberItemService memberItemService;

    private final IOmsMemberItemEService memberItemEService;

    @ApiOperation(value = "NFT列表")
    @GetMapping("/page")
    public PageResult<MemberItemVO> list(ItemPageQuery queryParams) {

        IPage<MemberItemVO> page = memberItemService.page(queryParams);
        return PageResult.success(page);
    }

    @ApiOperation(value = "开盲盒")
    @GetMapping("/{id}/open")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result<OpenItemVO> open(
            @ApiParam("id") @PathVariable Long id) {

        OpenItemVO openItemVO = memberItemService.open(id);
        if (openItemVO == null) {
            throw new BizException(ResultCode.ITEM_OPEN_NOTHING);
        }
        return Result.success(openItemVO);
    }

    @ApiOperation(value = "查询关联物品Id")
    @GetMapping("/{orderSn}/check")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result<String> checkItems(
            @ApiParam("orderSn") @PathVariable String orderSn) {
        String itemIds = memberItemService.checkByOrderSn(orderSn);
        return Result.success(itemIds);
    }

}

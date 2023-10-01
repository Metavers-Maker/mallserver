package com.muling.mall.wms.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.base.BasePageQuery;
import com.muling.common.result.PageResult;
import com.muling.mall.wms.pojo.vo.TransferLogVO;
import com.muling.mall.wms.service.IWmsTransferLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "app-转赠日志信息")
@RestController
@RequestMapping("/app-api/v1/transfer-log")
@RequiredArgsConstructor
public class TransferLogController {

    private final IWmsTransferLogService transferLogService;

    @ApiOperation(value = "列表")
    @GetMapping("/page")
    public PageResult<TransferLogVO> logList(BasePageQuery queryParams) {
        IPage<TransferLogVO> page = transferLogService.page(queryParams);
        return PageResult.success(page);
    }

}

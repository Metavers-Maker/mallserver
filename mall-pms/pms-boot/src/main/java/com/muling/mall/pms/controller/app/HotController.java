package com.muling.mall.pms.controller.app;

import com.muling.common.result.Result;
import com.muling.mall.pms.pojo.vo.HotVO;
import com.muling.mall.pms.es.service.PmsHotEService;
import com.muling.mall.pms.service.IPmsHotService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "app-热度信息")
@RestController
@RequestMapping("/app-api/v1/hots")
@RequiredArgsConstructor
public class HotController {

//    private final PmsHotEService hotEService;

    private final IPmsHotService hotService;

    @ApiOperation(value = "热度列表")
    @GetMapping
    public Result<List<HotVO>> list() {
        List<HotVO> lists = hotService.getLists();
        return Result.success(lists);
    }
}

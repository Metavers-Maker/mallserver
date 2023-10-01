package com.muling.global.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.global.pojo.entity.Contactus;
import com.muling.global.service.IContactusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "admin-联系我们")
@RestController("ContactusController")
@RequestMapping("/api/v1/contactus")
@RequiredArgsConstructor
public class ContactusController {

    private final IContactusService contactusService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam("名称") String name
    ) {
        LambdaQueryWrapper<Contactus> queryWrapper = new LambdaQueryWrapper<Contactus>()
                .like(StrUtil.isNotBlank(name), Contactus::getName, name)
                .orderByDesc(Contactus::getUpdated)
                .orderByDesc(Contactus::getCreated);
        Page<Contactus> result = contactusService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }
}

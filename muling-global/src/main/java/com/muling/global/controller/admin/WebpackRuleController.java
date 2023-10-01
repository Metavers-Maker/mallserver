//package com.muling.oss.controller.admin;
//
//import cn.hutool.core.util.StrUtil;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.muling.common.result.PageResult;
//import com.muling.common.result.Result;
//import com.muling.oss.pojo.entity.OssWebpackRule;
//import com.muling.oss.pojo.form.WebpackRuleForm;
//import com.muling.oss.service.IWebpackRuleService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import lombok.RequiredArgsConstructor;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Arrays;
//
//@Api(tags = "admin-更新规则")
//@RestController("WebpackRuleController")
//@RequestMapping("/api/v1/webpack-rule")
//@RequiredArgsConstructor
//public class WebpackRuleController {
//
//    private final IWebpackRuleService webpackRuleService;
//
//    @ApiOperation(value = "更新规则列表分页")
//    @GetMapping("/page")
//    public PageResult page(
//            @ApiParam(value = "页码", example = "1") Long pageNum,
//            @ApiParam(value = "每页数量", example = "10") Long pageSize,
//            @ApiParam("轮播名称") String name
//    ) {
//        LambdaQueryWrapper<OssWebpackRule> queryWrapper = new LambdaQueryWrapper<OssWebpackRule>()
//                .like(StrUtil.isNotBlank(name), OssWebpackRule::getName, name)
//                .orderByDesc(OssWebpackRule::getUpdated)
//                .orderByDesc(OssWebpackRule::getCreated);
//        Page<OssWebpackRule> result = webpackRuleService.page(new Page<>(pageNum, pageSize), queryWrapper);
//        return PageResult.success(result);
//    }
//
//    @ApiOperation(value = "新增更新规则")
//    @PostMapping
//    public Result add(
//            @RequestBody @Validated WebpackRuleForm addressForm
//    ) {
//        boolean result = webpackRuleService.add(addressForm);
//        return Result.judge(result);
//    }
//
//    @ApiOperation(value = "修改地址")
//    @PutMapping("/{webpackRuleId}")
//    public Result update(
//            @ApiParam(value = "地址ID") @PathVariable Long webpackRuleId,
//            @RequestBody @Validated WebpackRuleForm addressForm
//    ) {
//        boolean result = webpackRuleService.update(webpackRuleId, addressForm);
//        return Result.judge(result);
//    }
//
//    @ApiOperation(value = "删除地址")
//    @DeleteMapping("/{ids}")
//    public Result deleteAddress(
//            @ApiParam("地址ID，过个以英文逗号(,)分割") @PathVariable String ids
//    ) {
//        boolean status = webpackRuleService.removeByIds(Arrays.asList(ids.split(",")));
//        return Result.judge(status);
//    }
//}

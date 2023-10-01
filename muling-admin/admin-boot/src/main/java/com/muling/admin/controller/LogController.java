package com.muling.admin.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.admin.pojo.dto.SysLogDTO;
import com.muling.admin.pojo.entity.SysLog;
import com.muling.admin.service.ISysLogService;
import com.muling.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 *
 */
@Api(tags = "admin-系统日志")
@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
@Slf4j
public class LogController {

    private final ISysLogService logService;

    @ApiOperation(value = "分页列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", defaultValue = "1", value = "page", paramType = "query", dataType = "Integer", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "limit", defaultValue = "10", value = "size", paramType = "query", dataType = "Integer", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "started", value = "size", paramType = "query", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "ended", value = "size", paramType = "query", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "username", value = "username", paramType = "query", dataType = "String", dataTypeClass = String.class)
    })
    @GetMapping("/page")
    public Result pageList(Integer page, Integer limit, String username, String started, String ended) {
        Page<SysLog> p = new Page<>(page, limit);
        p.addOrder(OrderItem.desc("created"));
        IPage<SysLog> result = logService.list(p, username, started, ended);
        return Result.success(result.getRecords(), result.getTotal());
    }

    @ApiOperation(value = "导出", produces = "application/octet-stream")
    @GetMapping(value = "/export", produces = "application/octet-stream")
    public void export(HttpServletResponse response, String username, String started, String ended) throws IOException {
        // 这里URLEncoder.encode可以防止中文乱码
        String fileName = URLEncoder.encode("syslog_" + started + "-" + ended, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        response.setHeader("Content-type", "application/octet-stream");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        //新建ExcelWriter
        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).excelType(ExcelTypeEnum.XLSX).needHead(true).build();
        try {
            List<SysLogDTO> bidDetails = logService.exportList(username, started, ended);
            //获取sheet0对象
            WriteSheet mainSheet = EasyExcel.writerSheet(0, "syslog").head(SysLogDTO.class).build();
            //向sheet0写入数据 传入空list这样只导出表头
            excelWriter.write(bidDetails, mainSheet);
        } catch (Exception e) {
            log.error("export error {}", e.getMessage());
        } finally {
            //关闭流
            excelWriter.finish();
        }
    }

    //
}

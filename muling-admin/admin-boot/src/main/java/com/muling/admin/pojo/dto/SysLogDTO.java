package com.muling.admin.pojo.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.muling.admin.converter.LogOperateTypeEnumConverter;
import com.muling.admin.converter.LogTypeEnumConverter;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import lombok.Data;

import java.util.Date;

@Data
public class SysLogDTO {

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 耗时
     */
    private Long costTime;

    /**
     * IP
     */
    private String ip;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 请求类型
     */
    private String requestType;

    /**
     * 请求路径
     */
    private String requestUrl;
    /**
     * 请求方法
     */
    private String method;

    /**
     * 操作人用户名称
     */
    private String username;
    /**
     * 操作人用户账户
     */
    private Long userid;

    /**
     * 日志类型（1登录日志，2操作日志）
     */
    @ExcelProperty(value = "logType", converter = LogTypeEnumConverter.class)
    private LogTypeEnum logType;

    /**
     * 操作类型（1查询，2添加，3修改，4删除,5导入，6导出）
     */
    @ExcelProperty(value = "operateType", converter = LogOperateTypeEnumConverter.class)
    private LogOperateTypeEnum operateType;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date created;
}


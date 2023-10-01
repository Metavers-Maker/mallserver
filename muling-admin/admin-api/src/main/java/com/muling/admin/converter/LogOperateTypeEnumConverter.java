package com.muling.admin.converter;


import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.muling.common.base.IBaseEnum;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;

import java.text.ParseException;
import java.util.Date;

public class LogOperateTypeEnumConverter implements Converter<LogOperateTypeEnum> {
    public LogOperateTypeEnumConverter() {
    }

    public Class<?> supportJavaTypeKey() {
        return Date.class;
    }

    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    public LogOperateTypeEnum convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws ParseException {
        return contentProperty != null ? IBaseEnum.getEnumByValue(cellData.getStringValue(), LogOperateTypeEnum.class) : null;
    }

    public WriteCellData<?> convertToExcelData(LogOperateTypeEnum value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return contentProperty != null ? new WriteCellData(value.getLabel()) : new WriteCellData("");
    }
}

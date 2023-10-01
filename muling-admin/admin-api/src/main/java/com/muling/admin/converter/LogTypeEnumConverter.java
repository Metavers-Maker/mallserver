package com.muling.admin.converter;


import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.muling.common.base.IBaseEnum;
import com.muling.common.enums.LogTypeEnum;

import java.text.ParseException;
import java.util.Date;

public class LogTypeEnumConverter implements Converter<LogTypeEnum> {
    public LogTypeEnumConverter() {
    }

    public Class<?> supportJavaTypeKey() {
        return Date.class;
    }

    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    public LogTypeEnum convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws ParseException {
        return contentProperty != null ? IBaseEnum.getEnumByValue(cellData.getStringValue(), LogTypeEnum.class) : null;
    }

    public WriteCellData<?> convertToExcelData(LogTypeEnum value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return contentProperty != null ? new WriteCellData(value.getLabel()) : new WriteCellData("");
    }
}

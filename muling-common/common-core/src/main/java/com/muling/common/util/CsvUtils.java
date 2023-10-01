package com.muling.common.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.*;

public class CsvUtils {

    private static Logger logger = LoggerFactory.getLogger(CsvUtils.class);


    /**
     * 导出
     *
     * @param writer
     * @param data
     */
    public static void exportByObject(Writer writer, List<? extends Object> data){

        exportByObject(writer, null, data, null);
    }

    /**
     * 导出
     *
     * @param writer
     * @param headerList
     * @param data
     */
    public static void exportByObject(Writer writer, List<String> headerList, List<? extends Object> data){

        exportByObject(writer, headerList, data, null);
    }

    /**
     * 导出
     *
     * @param writer
     * @param headerList
     * @param data
     * @param writeProcessors
     */
    public static void exportByObject(Writer writer, List<String> headerList, List<? extends Object> data, CellProcessor[] writeProcessors){

        //将对象bean转为List<? extends Object>
        List<List<? extends Object>> list = new ArrayList<>();

        if (ValidateUtil.isNotEmpty(data)) {

            Field[] fields = ReflectUtil.getAllFields(data.get(0).getClass());

            for( Object bean : data) {
                Map map = MapUtil.fromBean(bean, false);

                List<Object> fieldList = new ArrayList<>();
                for (int i = 0; i < fields.length; i++){

                    fieldList.add(map.get(fields[i].getName()));
                }

                list.add(fieldList);
            }
        }

        export(writer, headerList, list, writeProcessors);
    }

    /**
     * 导出
     *
     * @param writer
     * @param data
     */
    public static void export(Writer writer, List<List<? extends Object>> data){

        export(writer, null, data, null);
    }

    /**
     * 导出
     *
     * @param writer
     * @param headerList
     * @param data
     */
    public static void export(Writer writer, List<String> headerList, List<List<? extends Object>> data, CellProcessor[] writeProcessors){

        exportWithSuperCsv(writer, headerList, data, writeProcessors);
    }

    //--------------------------------------impl-----------------------------------------------------------------
    /**
     * 导出
     *
     * @param writer java.io.Writer
     * @param headerList 头部
     * @param data 写入的数据
     * @param writeProcessors 格式设置，如new CellProcessor[] { null, new Optional(new FmtBool("Y", "N")), null, null}
     */
    public static void exportWithSuperCsv(Writer writer, List<String> headerList, List<List<? extends Object>> data, CellProcessor[] writeProcessors){

        CsvListWriter listWriter = null;

        try {
            listWriter = new CsvListWriter(writer, CsvPreference.STANDARD_PREFERENCE);

            //写入头部
            if (ValidateUtil.isNotEmpty(headerList)){
                String[] headers = new String[headerList.size()];
                headerList.toArray(headers);
                listWriter.writeHeader(headers);
            }

            for( List<?> bean : data) {

                if (ValidateUtil.isNotEmpty(writeProcessors)){
                    listWriter.write(bean, writeProcessors);
                }else {
                    listWriter.write(bean);
                }
            }
            listWriter.flush();
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }finally {
            try {
                if (null != listWriter){
                    listWriter.close();
                }
            } catch (IOException e) {
                logger.info(e.getMessage(), e);
            }
        }
    }
}

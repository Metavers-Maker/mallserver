package com.muling.admin.service.impl;

import com.muling.common.util.CsvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Component
public class ExcelExportHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelExportHelper.class);

    protected static final byte[] UFT_8_BOM_BYTES = {(byte) 0xef, (byte) 0xbb, (byte) 0xbf};

    public void export(List<?> data, List<String> headers, String url) {

        try {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            outputStream.write(UFT_8_BOM_BYTES);

            PrintWriter writer = new PrintWriter(outputStream);

            CsvUtils.exportByObject(writer, headers, data);


        } catch (IOException e) {
            LOGGER.error("get writer from failed", e);
        }
    }
}

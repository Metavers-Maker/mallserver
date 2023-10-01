package com.muling.common.web.util;

import com.muling.common.util.CsvUtils;
import jodd.util.StringPool;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.util.CsvContext;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.List;

public class ExcelExportUtil {
    protected static final byte[] UFT_8_BOM_BYTES = {(byte) 0xef, (byte) 0xbb, (byte) 0xbf};

    public static void exportCsv(HttpServletResponse response, List<String> headerList, List list, String fileName) throws IOException {
        response.setCharacterEncoding(StringPool.UTF_8);
        response.setContentType("application/csv;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

        ServletOutputStream os = response.getOutputStream();

        os.write(UFT_8_BOM_BYTES);

        PrintWriter writer = new PrintWriter(os);
        CellProcessor[] processors = new CellProcessor[headerList.size()];

        for (int i = 0; i < headerList.size(); i++) {
            processors[i] = new StringProcessor();
        }
        CsvUtils.exportByObject(writer, headerList, list, processors);
    }

    private static class StringProcessor extends CellProcessorAdaptor {
        @Override
        public <T> T execute(Object value, CsvContext context) {
            String stringValue = String.valueOf(value) + "\t";

            return next.execute(stringValue, context);
        }
    }
}

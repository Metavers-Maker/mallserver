package com.muling.global.controller.admin;

import com.aliyuncs.utils.IOUtils;
import com.muling.common.util.ImageUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;


@Api(tags = "system-image")
@RestController("ImageController")
@RequestMapping("/api/v1/image")
@Slf4j
@AllArgsConstructor
public class ImageController {

    @ApiOperation(value = "thumbnail", produces = MediaType.IMAGE_JPEG_VALUE)
    @ApiImplicitParams({@ApiImplicitParam(name = "url", value = "URL", paramType = "query", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "width", value = "width", paramType = "query", dataType = "Integer", defaultValue = "50", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "height", value = "height", paramType = "query", dataType = "Integer", defaultValue = "50", dataTypeClass = Integer.class)
    })
    @GetMapping(value = "/thumbnail", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@RequestParam String url, @RequestParam(defaultValue = "50") Integer width, @RequestParam(defaultValue = "50") Integer height) throws Exception {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            BufferedImage read = ImageIO.read(new URL(url));
            BufferedImage bufferedImage = ImageUtils.resizeImage(read, ImageUtils.IMAGE_JPEG, width, height);

            ImageIO.write(bufferedImage, "jpeg", stream);
        } finally {
            IOUtils.closeQuietly(stream);
        }
//        final HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.IMAGE_JPEG);
        return ResponseEntity.ok(stream.toByteArray());
    }


//    @ApiOperation(value = "下载文件")
//    @GetMapping(value = "/downFile", produces = MediaType.IMAGE_JPEG_VALUE)
//    public ResponseEntity<byte[]> downFile(HttpServletRequest request) throws Exception {
//        File file = new File("path");
//        //设置响应头
//        HttpHeaders headers = new HttpHeaders();
//        //通知浏览器以下载的方式打开文件
//        headers.setContentDispositionFormData("attachment", file.getName());
//        //定义以流的形式下载返回文件数据
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
//    }

//    @ApiOperation(value = "download")
//    @GetMapping(value = "/download")
//    public void download(HttpServletResponse response, String started, String ended) throws IOException {
//
//        // 这里URLEncoder.encode可以防止中文乱码
//        String fileName = URLEncoder.encode("template", "UTF-8").replaceAll("\\+", "%20");
//        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
//        response.setHeader("Content-type", "application/octet-stream");
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setCharacterEncoding("utf-8");
//
//        //新建ExcelWriter
//        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).excelType(ExcelTypeEnum.XLSX).needHead(true).build();
//        try {
//            //获取sheet0对象
//            WriteSheet mainSheet = EasyExcel.writerSheet(0, "采购单").head(Object.class).build();
//            //向sheet0写入数据 传入空list这样只导出表头
//            excelWriter.write(Lists.newArrayList(), mainSheet);
//            //获取sheet1对象
//            WriteSheet detailSheet = EasyExcel.writerSheet(1, "采购单明细").head(Object.class).build();
//            //向sheet1写入数据 传入空list这样只导出表头
//            excelWriter.write(Lists.newArrayList(), detailSheet);
//
//        } catch (Exception e) {
//            log.error("导出异常{}", e.getMessage());
//        } finally {
//            //关闭流
//            excelWriter.finish();
//        }
//    }
}

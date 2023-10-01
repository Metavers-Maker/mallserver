package com.muling.mall.ums.controller.app;

import cn.hutool.core.io.FileUtil;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.StorageClass;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.muling.common.result.Result;
import com.muling.common.util.MD5Util;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.ums.pojo.vo.STSVO;
import com.muling.mall.ums.service.IUmsMemberService;
import com.muling.mall.ums.util.OssManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "app-上传相关")
@RestController
@RequestMapping("/app-api/v1/upload")
@Slf4j
@AllArgsConstructor
public class UploadController {

    private final StringRedisTemplate stringRedisTemplate;

    private final IUmsMemberService memberService;

    private final RabbitTemplate rabbitTemplate;

    @ApiOperation(value = "获取上传的STS")
    @PostMapping("/upload/sts")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result getSts(@ApiParam("资源名称") @RequestParam String filename) {
        // STS接入地址，例如sts.cn-hangzhou.aliyuncs.com。
        String endpoint = "sts.cn-hangzhou.aliyuncs.com";
        // 填写步骤1生成的访问密钥AccessKey ID和AccessKey Secret。
        String AccessKeyId = "LTAI5tNGrcKGsN2foyFd476E";
        String accessKeySecret = "vHROCwupc7Yyioi4Ec3AvyLBWo6d9p";
        // 填写步骤3获取的角色ARN。
        String roleArn = "acs:ram::1435163731381985:role/ramoss";
        // 自定义角色会话名称，用来区分不同的令牌，例如可填写为SessionTest。
        String roleSessionName = "session-app-hiyuan";
        // 以下Policy用于限制仅允许使用临时访问凭证向目标存储空间examplebucket上传文件。
        // 临时访问凭证最后获得的权限是步骤4设置的角色权限和该Policy设置权限的交集，即仅允许将文件上传至目标存储空间examplebucket下的exampledir目录。
        String policy = "{\n" +
                "    \"Version\": \"1\", \n" +
                "    \"Statement\": [\n" +
                "        {\n" +
                "            \"Action\": [\n" +
                "                \"oss:PutObject\"\n" +
                "            ], \n" +
                "            \"Resource\": [\n" +
                "                \"acs:oss:*:*:hixmeta/*\" \n" +
                "            ], \n" +
                "            \"Effect\": \"Allow\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        OSS ossClient = null;
        STSVO stsvo = new STSVO();
        try {
            // regionId表示RAM的地域ID。以华东1（杭州）地域为例，regionID填写为cn-hangzhou。也可以保留默认值，默认值为空字符串（""）。
            String regionId = "cn-hangzhou";
            // 添加endpoint。适用于Java SDK 3.12.0及以上版本。
            DefaultProfile.addEndpoint(regionId, "Sts", endpoint);
            // 添加endpoint。适用于Java SDK 3.12.0以下版本。
            // DefaultProfile.addEndpoint("",regionId, "Sts", endpoint);
            // 构造default profile。
            IClientProfile profile = DefaultProfile.getProfile(regionId, AccessKeyId, accessKeySecret);
            // 构造client。
            DefaultAcsClient client = new DefaultAcsClient(profile);
            final AssumeRoleRequest request = new AssumeRoleRequest();
            // 适用于Java SDK 3.12.0及以上版本。
            request.setSysMethod(MethodType.POST);
            // 适用于Java SDK 3.12.0以下版本。
            //request.setMethod(MethodType.POST);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
            request.setPolicy(policy); // 如果policy为空，则用户将获得该角色下所有权限。
            request.setDurationSeconds(3600L); // 设置临时访问凭证的有效时间为3600秒。
            final AssumeRoleResponse response = client.getAcsResponse(request);
            //
            ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
            conf.setSupportCname(false);
            //
            String endpoint_oss = "http://oss-cn-hangzhou.aliyuncs.com";
            //oss客户端
            ossClient = new OSSClientBuilder().build(endpoint_oss,
                    response.getCredentials().getAccessKeyId(),
                    response.getCredentials().getAccessKeySecret(),
                    response.getCredentials().getSecurityToken());

            // 设置请求头。
            Map<String, String> headers = new HashMap<String, String>();
            headers.put(OSSHeaders.STORAGE_CLASS, StorageClass.Standard.toString());
            headers.put(OSSHeaders.CONTENT_TYPE, "image/jpeg");
            // 设置用户自定义元信息。
//            Map<String, String> userMetadata = new HashMap<String, String>();
            /*userMetadata.put("key1","value1");
            userMetadata.put("key2","value2");*/
            URL signedUrl = null;
            // 生成签名URL。
            String bucketName = "hixmeta";
            GeneratePresignedUrlRequest request_url = new GeneratePresignedUrlRequest(bucketName, filename, HttpMethod.PUT);
            Date expiration = new Date(new Date().getTime() + 3600 * 1000);
            request_url.setExpiration(expiration);
            request_url.setHeaders(headers);
//            request_url.setUserMetadata(userMetadata);
            signedUrl = ossClient.generatePresignedUrl(request_url);
//            // 设置签名URL过期时间，单位为毫秒。
//            Date expiration = new Date(new Date().getTime() + 3600 * 1000);
//            // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
//            URL url = ossClient.generatePresignedUrl("hixmeta", filename, expiration);
//            System.out.println(url);
            //拼接URL
            stsvo.setExpire(response.getCredentials().getExpiration());
            stsvo.setKey(response.getCredentials().getAccessKeyId());
            stsvo.setSecret(response.getCredentials().getAccessKeySecret());
            stsvo.setToken(response.getCredentials().getSecurityToken());
            //
            stsvo.setPutUrl(signedUrl);
            //
            System.out.println("Expiration: " + response.getCredentials().getExpiration());
            System.out.println("Access Key Id: " + response.getCredentials().getAccessKeyId());
            System.out.println("Access Key Secret: " + response.getCredentials().getAccessKeySecret());
            System.out.println("Security Token: " + response.getCredentials().getSecurityToken());
            System.out.println("RequestId: " + response.getRequestId());
        } catch (ClientException e) {
            System.out.println("Failed：");
            System.out.println("Error code: " + e.getErrCode());
            System.out.println("Error message: " + e.getErrMsg());
            System.out.println("RequestId: " + e.getRequestId());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return Result.success(stsvo);
    }


    /**
     * 上传文件
     *
     * @param multipartFile
     * @return
     */
    @ApiOperation(value = "上传图片")
    @RequestMapping(value = "/img", method = RequestMethod.POST)
    public Result<String> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        InputStream inputStream = null;
        try {
            inputStream = multipartFile.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //拼接文件夹名字会指定上传到存储空间下面的子文件夹(文件名改为：当前时间到毫秒+uuid+原来文件后缀名)
        //时间戳计算路径名称
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String now_time = dtf.format(LocalDateTime.now());
        String originalFilename = "pay/" + now_time + '/';
        //文件名称
        Long memberId = MemberUtils.getMemberId();
        String fname = System.currentTimeMillis() + multipartFile.getOriginalFilename() + memberId.toString();
        fname = MD5Util.encodeMD5(fname);
        String ext = ".jpeg";
        String fname_src = multipartFile.getOriginalFilename();
        ext = fname_src.substring(fname_src.lastIndexOf("."));
        //最后路径名称
        String fullPath = originalFilename + fname + ext;
        //使用封装的工具类进行上传
        OssManager.uploadImage(fullPath, multipartFile.getSize(), inputStream);

        String data = OssManager.bucketDomain + "/" + fullPath;
        System.out.println("=========================");

        System.out.println(data);

        return Result.success(data);

    }

//    public static void main(String[] args) {
//        File file = new File("/Users/sunke/0.jpeg");
//        BufferedInputStream inputStream = FileUtil.getInputStream(file);
//        //拼接文件夹名字会指定上传到存储空间下面的子文件夹(文件名改为：当前时间到毫秒+uuid+原来文件后缀名)
//        String originalFilename = "img/"
//                + System.currentTimeMillis()
//                + ".jpeg";
//        //使用封装的工具类进行上传
//        OssManager.uploadImage(originalFilename, file.length(), inputStream);
//
//        String data = OssManager.bucketDomain + "/" + originalFilename;
//        System.out.println(data);
//    }

}

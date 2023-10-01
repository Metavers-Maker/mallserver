package com.muling.mall.ums.controller.admin;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
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
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.muling.common.base.IBaseEnum;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.enums.VCodeTypeEnum;
import com.muling.common.exception.BizException;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.util.VCodeUtils;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.mall.ums.event.MemberAuthSuccessEvent;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.pojo.vo.STSVO;
import com.muling.mall.ums.service.IUmsMemberService;
import com.muling.mall.ums.test.Message;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "admin-上传相关")
@RestController("AdminUploadController")
@RequestMapping("/api/v1/upload")
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
        String AccessKeyId = "LTAI5tNGrcKGsN2foyFd476E";
        String accessKeySecret = "vHROCwupc7Yyioi4Ec3AvyLBWo6d9p";
        String roleArn = "acs:ram::1435163731381985:role/ramoss";
        // 自定义角色会话名称，用来区分不同的令牌
        String roleSessionName = "session-admin-hiyuan";
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
            DefaultProfile.addEndpoint(regionId, "Sts", endpoint);
            IClientProfile profile = DefaultProfile.getProfile(regionId, AccessKeyId, accessKeySecret);
            DefaultAcsClient client = new DefaultAcsClient(profile);
            final AssumeRoleRequest request = new AssumeRoleRequest();
            request.setSysMethod(MethodType.POST);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
            request.setPolicy(null); // 如果policy为空，则用户将获得该角色下所有权限。
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
            URL signedUrl = null;
            String bucketName = "hixmeta";
            GeneratePresignedUrlRequest request_url = new GeneratePresignedUrlRequest(bucketName, filename, HttpMethod.PUT);
            Date expiration = new Date(new Date().getTime() + 3600 * 1000);
            request_url.setExpiration(expiration);
            request_url.setHeaders(headers);
            signedUrl = ossClient.generatePresignedUrl(request_url);
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

}

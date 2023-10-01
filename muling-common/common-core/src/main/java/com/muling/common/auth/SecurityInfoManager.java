//package com.muling.common.auth;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.Base64;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.amazonaws.services.secretsmanager.AWSSecretsManager;
//import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
//import com.amazonaws.services.secretsmanager.model.DecryptionFailureException;
//import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
//import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
//import com.amazonaws.services.secretsmanager.model.InternalServiceErrorException;
//import com.amazonaws.services.secretsmanager.model.InvalidRequestException;
//
//import software.amazon.awssdk.services.cloudwatchlogs.model.InvalidParameterException;
//import software.amazon.awssdk.services.cloudwatchlogs.model.ResourceNotFoundException;
//
//public class SecurityInfoManager {
//
//    private static final Logger log = LoggerFactory.getLogger(SecurityInfoManager.class);
//
//    private static Map<String,String> keyMap = new HashMap<String,String>();
//
//    private static Properties props = new Properties();
//    static{
//        String keys = "{\n" +
//                "  \"PWD_SALT\": \"$2a$10$EcZTIciuZeQaVhIyRsOc/e\\\\n\",\n" +
//                "  \"SIGN_SECRET\": \"XFmGZtq8JOl29vG$lm*JgONwHhVDO4OqMs4AfxOzSsQbNg^si2Qg#t#e&w6cFQkq\",\n" +
//                "  \"TOKEN_SECRET\": \"%#`=I=Q*f^khuo90LKSS..;'193VE&^H??][zmus\",\n" +
//                "  \"REQ_RSP_SECRET\": \"!1qI%ZIp7GMrUYDi\",\n" +
//                "  \"WALLET_HTTP_SECURET\": \"81dbc48a540b3569d2fa9cbaad118af6\",\n" +
//                "  \"GOOGLE_AUTH_SEED\": \"g8GjEvTbW5oVSV7avL47357438reyhreyuryetredLDVKs2m0QN7vxRs2im5MDaNCWGmcD2rvcZx\",\n" +
//                "  \"DATASOURCE_SHOW_BIZ\": \"{\\\"userName\\\":\\\"show_biz\\\",\\\"password\\\":\\\"6QON$Md9!Onf$l\\\"}\",\n" +
//                "  \"DATASOURCE_SHOW_BIZ_PAY\": \"{\\\"userName\\\":\\\"show_biz\\\",\\\"password\\\":\\\"6QON$Md9!Onf$l\\\"}\",\n" +
//                "  \"REDIS_PWD\": \"PlVzD4ctJ6mfYyX123!\",\n" +
//                "  \"ALIYUN_OSS_KEY\": \"LTAIwtGrrcnW2Vzh\",\n" +
//                "  \"ALIYUN_OSS_SECRET\": \"hCuoBEEFfQvGNzhk1OgS8WdWoGIG8A\",\n" +
//                "  \"ALIYUN_SDK_KEY\": \"LTAI2wZExxlG7dmp\",\n" +
//                "  \"ALIYUN_SDK_SECRET\": \"yvSkpzYKJmycdJUk5evFcEySox5b55\",\n" +
//                "  \"ALIYUN_VOD_KEY\": \"LTAIv5j4jHsk4k8P\",\n" +
//                "  \"ALIYUN_VOD_SECRET\": \"okfyHMYt2QwexFzXC3UU06oGUvgMLP\",\n" +
//                "  \"ALIYUN_COM_KEY\": \"LTAIG1UKeRO5AAp0\",\n" +
//                "  \"ALIYUN_COM_SECRET\": \"B1dYlqFBIeX31XZkaWhZLSjiXy11EU\",\n" +
//                "  \"YUNTONG_ACCOUNT_SID\": \"8a216da8627648690162a45328e310aa\",\n" +
//                "  \"YUNTONG_AUTH_TOKEN\": \"fa07bc9b25844f039d3ebab53e13a62b\",\n" +
//                "  \"AWS_ACCESS_KEY_ID\": \"AKIAYUXAYE2XJ5FGWGNZ\",\n" +
//                "  \"AWS_SECRET_ACCESS_KEY\": \"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\",\n" +
//                "  \"RONGYUN_KEY\": \"y745wfm8yjq7v\",\n" +
//                "  \"RONGYUN_SECRET\": \"RE96ZuejZUyUJl\",\n" +
//                "  \"MAIL_USER\": \"noreply@show.one\",\n" +
//                "  \"MAIL_PASSWORD\": \"qwKYPBmk2co57ich\",\n" +
//                "  \"DATASOURCE_SHOW_RELATION\": \"{\\\"userName\\\":\\\"show_biz\\\",\\\"password\\\":\\\"6QON$Md9!Onf$l\\\"}\",\n" +
//                "  \"USER_SIGNATURE_SECRET\": \"fSDkd}#@($!@i73-%#$*$(@!Jw.90\",\n" +
//                "  \"STOCK_LOG_SECRET\": \"sSDLFKJkd}#@($!@iafwef723-%#$*$(@!Jfew90\",\n" +
//                "  \"ALIYUN_SMS_KEY\": \"LTAI4Fo1QhAwkS7QqfHTKd8a\",\n" +
//                "  \"ALIYUN_SMS_SECRET\": \"i2MRQdqPJsGugbPl6YzCKS3L9NJfQ8\",\n" +
//                "  \"DATASOURCE_SHOW_JOB\": \"{\\\"userName\\\":\\\"show_biz\\\",\\\"password\\\":\\\"6QON$Md9!Onf$l\\\"}\"\n" +
//                "}";
//        keyMap = JacksonUtil.readJsonToObject(Map.class, keys);
//
//    }
//
//    /**加密算法盐  "$2a$10$EcZTIciuZeQaVhIyRsOc/e\n"  */
//    public static final String PWD_SALT = "PWD_SALT";
//    /**请求签名的密钥  "AEQZ#9lEjh9J&7zqq0MK3eMQR3iSqbqe4nq^@l6TQ&MWbu6n7A9NN3HRbUvFbflW"  */
//    public static final String SIGN_SECRET = "SIGN_SECRET";
//    /**token签名的密钥   "%#`=I=Q*f^khuo90LKSS..;'193VE&^H??][zmus"  */
//    public static final String TOKEN_SECRET = "TOKEN_SECRET";
//    /**请求和响应加密key   !1qI%ZIp7GMrUYDi*/
//    public static final String REQ_RSP_SECRET = "REQ_RSP_SECRET";
//    /**访问钱包系统密钥   81dbc48a540b3569d2fa9cbaad118af6*/
//    public static final String WALLET_HTTP_SECURET="WALLET_HTTP_SECURET";
//
//    /**user签名密钥   fSDkd}#@($!@i73-%#$*$(@!Jw.90*/
//    public static final String USER_SIGNATURE_SECRET="USER_SIGNATURE_SECRET";
//    /**stocklog签名密钥   sSDLFKJkd}#@($!@iafwef723-%#$*$(@!Jfew90*/
//    public static final String STOCK_LOG_SECRET="STOCK_LOG_SECRET";
//
//    /**google验证码种子  "g8GjEvTbW5oVSV7avL47357438reyhreyuryetredLDVKs2m0QN7vxRs2im5MDaNCWGmcD2rvcZx"*/
//    public static final String GOOGLE_AUTH_SEED = "GOOGLE_AUTH_SEED";
//    /**
//     * 数据库用户名和密码配置名
//     * {"host":"rm-2zerq5au39gl5t55i.mysql.rds.aliyuncs.com","port":3306,"userName":"showdb","password":"236d02bb5a85503bb915087cd26850a8674f48f0ffe5a5ba","maxPoolSize":100,"minPoolSize":5}
//     */
//    public static final String DATASOURCE = "DATASOURCE_";
//
//    /**redis密码    “PlVzD4ctJ6mfYyX123!”*/
//    public static final String REDIS_PWD = "REDIS_PWD";
//
//
//    /**aliyun oss key:LTAIwtGrrcnW2Vzh*/
//    public static final String ALIYUN_OSS_KEY = "ALIYUN_OSS_KEY";
//    /**aliyun oss secret:hCuoBEEFfQvGNzhk1OgS8WdWoGIG8A*/
//    public static final String ALIYUN_OSS_SECRET = "ALIYUN_OSS_SECRET";
//    /**aliyun sdk key:LTAI2wZExxlG7dmp*/
//    public static final String ALIYUN_SDK_KEY = "ALIYUN_SDK_KEY";
//    /**aliyun sdk secret:yvSkpzYKJmycdJUk5evFcEySox5b55*/
//    public static final String ALIYUN_SDK_SECRET = "ALIYUN_SDK_SECRET";
//    /**aliyun vod key:LTAIv5j4jHsk4k8P*/
//    public static final String ALIYUN_VOD_KEY = "ALIYUN_VOD_KEY";
//    /**aliyun vod secret:okfyHMYt2QwexFzXC3UU06oGUvgMLP*/
//    public static final String ALIYUN_VOD_SECRET = "ALIYUN_VOD_SECRET";
//    /**aliyun电话服务 key:LTAIG1UKeRO5AAp0*/
//    public static final String ALIYUN_COM_KEY="ALIYUN_COM_KEY";
//    /**aliyun电话服务 secret:B1dYlqFBIeX31XZkaWhZLSjiXy11EU*/
//    public static final String ALIYUN_COM_SECRET="ALIYUN_COM_SECRET";
//
//
//    /**云通讯账户sid   8a216da8627648690162a45328e310aa*/
//    public static final String YUNTONG_ACCOUNT_SID = "YUNTONG_ACCOUNT_SID";
//    /**云通讯账户auth token    fa07bc9b25844f039d3ebab53e13a62b */
//    public static final String YUNTONG_AUTH_TOKEN = "YUNTONG_AUTH_TOKEN";
//
//    /**阿里云短信access key*/
//    public static final String ALIYUN_SMS_KEY = "ALIYUN_SMS_KEY";
//    /**阿里云短信access secret*/
//    public static final String ALIYUN_SMS_SECRET = "ALIYUN_SMS_SECRET";
//
//    /**AWS DTS访问key id    AKIAYUXAYE2XJ5FGWGNZ*/
//    public static final String AWS_ACCESS_KEY_ID="AWS_ACCESS_KEY_ID";
//    /**AWS DTS访问key   GDVWz+6ao/xuu1/jMh/ri7CjQG4sEOThyAlWKZrm */
//    public static final String AWS_SECRET_ACCESS_KEY= "AWS_SECRET_ACCESS_KEY";
//
//    /**融云key  sfci50a7s83fi*/
//    public static final String RONGYUN_KEY = "RONGYUN_KEY";
//    /**融云secret  2BaHRQcACQqf*/
//    public static final String RONGYUN_SECRET = "RONGYUN_SECRET";
//
//    /**邮箱用户名*/
//    public static final String MAIL_USER="MAIL_USER";
//    /**邮箱密码*/
//    public static final String MAIL_PASSWORD="MAIL_PASSWORD";
//
//    public static void main(String[] args) {
//
//        System.out.println(getKeyValue("DATASOURCE_SHOW_JOB"));
//    }
//
//    public static String getKeyValue(String keyName) {
//        return keyMap.get(keyName);
//    }
//
//}

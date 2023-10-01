package com.muling.common.cert.service;

import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muling.common.cert.config.SandConfig;
import com.muling.common.cert.util.sand.CertUtil;
import com.muling.common.cert.util.sand.EncryptUtil;
import com.muling.common.cert.util.sand.RSAUtil;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HttpApiClientSand {

    private final CloseableHttpClient client;

    private final SandConfig sandConfig;

    private Logger log = LoggerFactory.getLogger(getClass());

    public static final ObjectMapper mapper = new ObjectMapper();

    private boolean localCheck(List<NameValuePair> formParams) throws Exception {
        //test
        String sendData = "";
        String sendSign = "";
        for (int i = 0; i < formParams.size(); i++) {
            if (formParams.get(i).getName().equals("data")) {
                sendData = formParams.get(i).getValue();
            }
            if (formParams.get(i).getName().equals("sign")) {
                sendSign = formParams.get(i).getValue();
            }
        }
        return EncryptUtil.decryptStoreSendData(sendData, sendSign);
    }

    private JSONObject buildHead(String method) {
        //
        DateFormat fmt = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
        String reqTime = fmt.format(System.currentTimeMillis());
        //
        JSONObject headParam = new JSONObject();
        headParam.put("version", "1.0");
        headParam.put("method", method);
        headParam.put("productId", "00000018"); //后台绑卡快捷
        headParam.put("accessType", "1");   //商户接入类型（普通商户）
        headParam.put("mid", sandConfig.getMid());  //企业ID
        headParam.put("plMid", ""); //核心企业ID（平台商户需要填写）
        headParam.put("channelType", "07");
        headParam.put("reqTime", reqTime);
        return headParam;
    }

    /**
     * 申请绑卡
     */
    public JSONObject bindCard(JSONObject bodyParam) {
        JSONObject ret = null;
        String method = "sandPay.fastPay.apiPay.applyBindCard";
        String path = "/fastPay/apiPay/applyBindCard";
        JSONObject headParam = this.buildHead(method);
        JSONObject data = new JSONObject();
        data.put("head", headParam);
        data.put("body", bodyParam);
        return this.post(data, path);
    }

    /**
     * 确认绑卡
     */
    public JSONObject bindCardEnsure(JSONObject bodyParam) {
        JSONObject ret = null;
        String method = "sandPay.fastPay.apiPay.confirmBindCard";
        String path = "/fastPay/apiPay/confirmBindCard";
        JSONObject headParam = this.buildHead(method);
        JSONObject data = new JSONObject();
        data.put("head", headParam);
        data.put("body", bodyParam);
        return this.post(data, path);
    }

    /**
     * 取消绑卡
     */
    public JSONObject unbindCard(JSONObject bodyParam) {
        JSONObject ret = null;
        String method = "sandPay.fastPay.apiPay.unbindCard";
        String path = "/fastPay/apiPay/unbindCard";
        JSONObject headParam = this.buildHead(method);
        JSONObject data = new JSONObject();
        data.put("head", headParam);
        data.put("body", bodyParam);
        return this.post(data, path);
    }

    /**
     * 支付申请
     */
    public JSONObject payApply(JSONObject bodyParam) {
        JSONObject ret = null;
        String method = "sandPay.fastPay.common.sms";
        String path = "/fastPay/apiPay/sms";
        JSONObject headParam = this.buildHead(method);
        JSONObject data = new JSONObject();
        data.put("head", headParam);
        data.put("body", bodyParam);
        return this.post(data, path);
    }

    /**
     * 支付申请
     */
    public JSONObject pay(JSONObject bodyParam) {
        JSONObject ret = null;
        String method = "sandPay.fastPay.apiPay.pay";
        String path = "/fastPay/apiPay/pay";
        JSONObject headParam = this.buildHead(method);
        JSONObject data = new JSONObject();
        data.put("head", headParam);
        data.put("body", bodyParam);
        return this.post(data, path);
    }

    /**
     * 分账支付
     */
    public JSONObject payDispatch(JSONObject bodyParam) {
        String method = "sandPay.fastPay.apiPay.pay";
        String path = "/fastPay/apiPay/pay";
        JSONObject headParam = this.buildHead(method);
        JSONObject data = new JSONObject();
        data.put("head", headParam);
        data.put("body", bodyParam);
        return this.post(data, path);
    }

    /**
     * 发送消息
     */
    public JSONObject post(JSONObject data, String path) {
        JSONObject ret = null;
        try {
            List<NameValuePair> formParams = EncryptUtil.getEncryptGateWayData(data.toJSONString(), "");
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
            //发送消息
            String url = sandConfig.getHost() + path;
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(uefEntity);
            CloseableHttpResponse response = client.execute(httpPost);
            if (response != null) {
                HttpEntity entity = response.getEntity();
                String responseContent = EntityUtils.toString(entity, "UTF-8");
                if (responseContent != null) {
                    String retStr = EncryptUtil.decryptGateWayRetData(URLUtil.decode(responseContent));
                    ret = JSONObject.parseObject(retStr);
                }
                EntityUtils.consume(response.getEntity());
            }
        } catch (Exception e) {
            log.info("sand err", e);
        }
        return ret;
    }

    /**
     * 封装版本，sand支付
     */
    public String createAccount(
            String ip,
            String userId,
            String nickName,
            String orderNo,
            boolean dev
    ) {
        //
        String version = "10";
        String createTime = RSAUtil.createTime();
        String mid = sandConfig.getMid();
        String customerOrderNo = orderNo;
        String order_amt = "0.0";
        ip = ip.replace(".", "_");
        String notify_url = "";
        String return_url = "http://shandewallet.creat.com";
        if (dev) {
            //开发版本
            notify_url = "https://i72558b438.zicp.fun/mall-ums/app-api/v1/sand/account/callback";
        } else {
            //生产版本
            notify_url = "https://link2meta-api.link2meta.cn/mall-ums/app-api/v1/sand/account/callback";
        }
        //支付域
        JSONObject extra = new JSONObject();
        extra.put("userId", userId);
        extra.put("nickName", nickName);
        extra.put("accountType", "1");
        //支付扩展域
        //"userId":"用户在商户系统中的唯一编号", "nickName":"会员昵称","accountType"："账户类型"  （选填）
//            String pay_extra = "{\"userId\":\"666666\",\"nickName\":\"张三\",\"accountType\":\"1\"}";
        String pay_extra = extra.toJSONString();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("accsplit_flag", "NO"); //是否分账的标志
        map.put("create_ip", ip);
        map.put("create_time", createTime);
        map.put("mer_no", mid);
        map.put("mer_order_no", customerOrderNo);
        map.put("notify_url", notify_url);
        map.put("order_amt", order_amt);
        map.put("pay_extra", pay_extra);
        map.put("return_url", return_url);
        map.put("sign_type", "RSA");
        map.put("store_id", "000000");
        map.put("version", version);
        //
        try {
            //
            String content = RSAUtil.getSignContent(map);
            log.info("参与签名字符串：{}", content);

            String sign = RSAUtil.sign(content, CertUtil.getPrivateKey());
            log.info("签名串：{}", sign);

            //拼接url
            String endTime = RSAUtil.endTime();
            String url = "https://faspay-oss.sandpay.com.cn/pay/h5/cloud?" +
                    //云函数h5： applet  ；支付宝H5：alipay  ； 微信公众号H5：wechatpay   ；
                    //一键快捷：fastpayment   ；H5快捷 ：unionpayh5    ；支付宝扫码：alipaycode ;快捷充值:quicktopup
                    //电子钱包【云账户】：cloud
                    "version=" + version + "" +
                    "&mer_no=" + mid + "" +
                    "&mer_order_no=" + customerOrderNo + "" +
                    "&create_time=" + createTime + "" +
                    "&expire_time=" + endTime + "" +  //endTime
                    "&order_amt=" + order_amt + "" +
                    "&notify_url=" + URLEncoder.encode(notify_url, "UTF-8") + "" +
                    "&return_url=" + URLEncoder.encode(return_url, "UTF-8") + "" +
                    "&create_ip=" + ip + "" +
                    "&goods_name=" + URLEncoder.encode("测试", "UTF-8") + "" +
                    "&store_id=000000" +
                    //产品编码: 云函数h5：  02010006  ；支付宝H5：  02020002  ；微信公众号H5：02010002   ；
                    //一键快捷：  05030001  ；H5快捷：  06030001   ；支付宝扫码：  02020005 ；快捷充值：  06030003
                    //电子钱包【云账户】：开通账户并支付product_code应为：04010001；消费（C2C）product_code 为：04010003 ; 我的账户页面 product_code 为：00000001
                    "&product_code=00000001" + "" +
                    "&clear_cycle=3" +
                    "&pay_extra=" + URLEncoder.encode(pay_extra, "UTF-8") + "" +
                    "&meta_option=%5B%7B%22s%22%3A%22Android%22,%22n%22%3A%22wxDemo%22,%22id%22%3A%22com.pay.paytypetest%22,%22sc%22%3A%22com.pay.paytypetest%22%7D%5D" +
                    "&accsplit_flag=NO" +
                    "&jump_scheme=" +
                    "&sign_type=RSA" +
                    "&sign=" + URLEncoder.encode(sign, "UTF-8") + "";
            log.info("url:{}", url);
            return url;
        } catch (UnsupportedEncodingException e) {
            //
        }
        return null;
    }

    /**
     * c2b 分账
     */
    public String c2bBuy(
            String ip,
            String userId,
            String nickName,
            String orderSn,
            String goodsName,
            Long payAmount,
            boolean dev
    ) {

        String createTime = RSAUtil.createTime();
        String endTime = RSAUtil.endTime();

        String version = "10";
        //商户号
        String mer_no = sandConfig.getMid();
        //订单号
        String mer_order_no = orderSn;
        //回调地址
        String notify_url = "";
        String return_url = "http://shandewallet.creat.com";
        if (dev) {
            //开发版本
            notify_url = "https://i72558b438.zicp.fun/mall-ums/app-api/v1/sand/account/callback";
        } else {
            //生产版本
            notify_url = "https://link2meta-api.link2meta.cn/mall-ums/app-api/v1/sand/account/callback";
        }
        //金额
        String order_amt = payAmount.toString();
        //商品名称
        String goods_name = goodsName;
        ip = ip.replace(".", "_");
        //支付扩展域
        JSONObject extra = new JSONObject();
        extra.put("userId", userId);
        extra.put("nickName", nickName);
        extra.put("accountType", "1");
        //"userId":"用户在商户系统中的唯一编号", "nickName":"会员昵称","accountType"："账户类型"  （选填）
        String pay_extra = extra.toJSONString();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("accsplit_flag", "NO");
        map.put("create_ip", ip);
        map.put("create_time", createTime);
        map.put("mer_no", mer_no);
        map.put("mer_order_no", mer_order_no);
        map.put("notify_url", notify_url);
        map.put("order_amt", order_amt);
        map.put("pay_extra", pay_extra);
        map.put("return_url", return_url);
        map.put("sign_type", "RSA");
        map.put("store_id", "000000");
        map.put("version", version);
        //map.put("expire_time",endTime);
        //map.put("goods_name",goods_name);
        //map.put("product_code","02010006");
        //map.put("clear_cycle","0");
        try {
            String content = RSAUtil.getSignContent(map);
            log.info("参与签名字符串：{}", content);
            String sign = RSAUtil.sign(content, CertUtil.getPrivateKey());
            log.info("签名串：{}", content);
            //拼接url
            String url = "https://faspay-oss.sandpay.com.cn/pay/h5/cloud?" +
                    //云函数h5： applet  ；支付宝H5：alipay  ； 微信公众号H5：wechatpay   ；
                    //一键快捷：fastpayment   ；H5快捷 ：unionpayh5    ；支付宝扫码：alipaycode ;快捷充值:quicktopup
                    //电子钱包【云账户】：cloud
                    "version=" + version + "" +
                    "&mer_no=" + mer_no + "" +
                    "&mer_order_no=" + mer_order_no + "" +
                    "&create_time=" + createTime + "" +
                    "&expire_time=" + endTime + "" +  //endTime
                    "&order_amt=" + order_amt + "" +
                    "&notify_url=" + URLEncoder.encode(notify_url, "UTF-8") + "" +
                    "&return_url=" + URLEncoder.encode(return_url, "UTF-8") + "" +
                    "&create_ip=" + ip + "" +
                    "&goods_name=" + URLEncoder.encode(goods_name, "UTF-8") + "" +
                    "&store_id=000000" +
                    //产品编码: 云函数h5：  02010006  ；支付宝H5：  02020002  ；微信公众号H5：02010002   ；
                    //一键快捷：  05030001  ；H5快捷：  06030001   ；支付宝扫码：  02020005 ；快捷充值：  06030003
                    //电子钱包【云账户】：开通账户并支付product_code应为：04010001；消费（C2C）product_code 为：04010003 ; 我的账户页面 product_code 为：00000001
                    "&product_code=04010001" + "" +
                    "&clear_cycle=3" +
                    "&pay_extra=" + URLEncoder.encode(pay_extra, "UTF-8") + "" +
                    "&meta_option=%5B%7B%22s%22%3A%22Android%22,%22n%22%3A%22wxDemo%22,%22id%22%3A%22com.pay.paytypetest%22,%22sc%22%3A%22com.pay.paytypetest%22%7D%5D" +
                    "&accsplit_flag=NO" +
                    "&jump_scheme=" +
                    "&sign_type=RSA" +
                    "&sign=" + URLEncoder.encode(sign, "UTF-8") + "";
            log.info("url:{}", url);
            return url;
        } catch (UnsupportedEncodingException e) {

        }
        return null;

    }

    /**
     * c2c 分账
     */
    public String c2cBuy(
            String ip,
            String buyerUserId,
            String sellerUserId,
            String orderSn,
            String goodsName,
            Long payAmount,
            String feeCount,
            boolean dev
    ) {
        String createTime = RSAUtil.createTime();
        String endTime = RSAUtil.endTime();
        String version = "10";
        //商户号
        String mer_no = sandConfig.getMid();
        //订单号
        String mer_order_no = orderSn;
        //回调地址
        String notify_url = "";
        String return_url = "http://shandewallet.creat.com";
        if (dev) {
            //开发版本
            notify_url = "https://i72558b438.zicp.fun/mall-ums/app-api/v1/sand/account/callback";
        } else {
            //生产版本
            notify_url = "https://link2meta-api.link2meta.cn/mall-ums/app-api/v1/sand/account/callback";
        }
        //金额
        String order_amt = payAmount.toString();
        //商品名称
        String goods_name = goodsName;
        ip = ip.replace(".", "_");
        //支付扩展域
        //"operationType":"操作类型",//  1:转账申请 2:确认收款 3:转账退回
        //"recvUserId":"收款方会员编号", //所有操作类型必填参数
        //"remark":"备注" //非必填
        //当operationType为1时参数按照下面说明填：return_url,notify_url为必填参数
        //"bizType":"转账类型", //必填 1：转账确认模式 2：实时转账模式
        //"payUserId":"付款方会员编号，用户在商户系统中的唯一编号 ；",//必填
        //"userFeeAmt":"用户服务费，商户向用户收取的服务费 ",//非必填
        //"postscript":"附言",// 非必填
        BigDecimal totalPay = BigDecimal.valueOf(payAmount);
        //支付扩展域
        JSONObject extra = new JSONObject();
        extra.put("operationType", "1");
        extra.put("recvUserId", sellerUserId);
        extra.put("bizType", "2");
        extra.put("payUserId", buyerUserId);
        extra.put("userFeeAmt", feeCount);
        //
        String pay_extra = extra.toJSONString();
        //
        Map<String, String> map = new LinkedHashMap<>();
        map.put("accsplit_flag", "NO");
        map.put("create_ip", ip);
        map.put("create_time", createTime);
        map.put("mer_no", mer_no);
        map.put("mer_order_no", mer_order_no);
        map.put("notify_url", notify_url);
        map.put("order_amt", order_amt);
        map.put("pay_extra", pay_extra);
        map.put("return_url", return_url);
        map.put("sign_type", "RSA");
        map.put("store_id", "000000");
        map.put("version", version);
        //map.put("expire_time", endTime);
        //map.put("goods_name", goods_name);
        //map.put("product_code", "04010003");
        //map.put("clear_cycle","0");
        try {
            String content = RSAUtil.getSignContent(map);
            log.info("参与签名字符串：{}", content);

            String sign = RSAUtil.sign(content, CertUtil.getPrivateKey());
            log.info("签名串：{}", content);

            //拼接url
            String url = "https://faspay-oss.sandpay.com.cn/pay/h5/cloud?" +
                    //云函数h5： applet  ；支付宝H5：alipay  ； 微信公众号H5：wechatpay   ；
                    //一键快捷：fastpayment   ；H5快捷 ：unionpayh5    ；支付宝扫码：alipaycode ;快捷充值:quicktopup
                    //电子钱包【云账户】：cloud
                    "version=" + version + "" +
                    "&mer_no=" + mer_no + "" +
                    "&mer_order_no=" + mer_order_no + "" +
                    "&create_time=" + createTime + "" +
                    "&expire_time=" + endTime + "" +  //endTime
                    "&order_amt=" + order_amt + "" +
                    "&notify_url=" + URLEncoder.encode(notify_url, "UTF-8") + "" +
                    "&return_url=" + URLEncoder.encode(return_url, "UTF-8") + "" +
                    "&create_ip=" + ip + "" +
                    "&goods_name=" + URLEncoder.encode(goods_name, "UTF-8") + "" +
                    "&store_id=000000" +
                    //产品编码: 云函数h5：  02010006  ；支付宝H5：  02020002  ；微信公众号H5：02010002   ；
                    //一键快捷：  05030001  ；H5快捷：  06030001   ；支付宝扫码：  02020005 ；快捷充值：  06030003
                    //电子钱包【云账户】：开通账户并支付product_code应为：04010001；消费（C2C）product_code 为：04010003 ; 我的账户页面 product_code 为：00000001
                    "&product_code=04010003" + "" +
                    "&clear_cycle=3" +
                    "&pay_extra=" + URLEncoder.encode(pay_extra, "UTF-8") + "" +
                    "&meta_option=%5B%7B%22s%22%3A%22Android%22,%22n%22%3A%22wxDemo%22,%22id%22%3A%22com.pay.paytypetest%22,%22sc%22%3A%22com.pay.paytypetest%22%7D%5D" +
                    "&accsplit_flag=NO" +
                    "&jump_scheme=" +
                    "&sign_type=RSA" +
                    "&sign=" + URLEncoder.encode(sign, "UTF-8") + "";
            log.info("最终链接：{}", url);
            return url;
        } catch (Exception e) {

        }
        return null;
    }
    //

}

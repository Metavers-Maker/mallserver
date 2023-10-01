package com.muling.mall.ums.util;

import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * 100	提交成功
 * 400	APIKey错误
 * 401	返回类型错误
 * 402	查询失败
 * 403	访问次数超限
 * 404	API参数错误{0}
 * 405	账户余额不足
 * 406	请确认您的访问地址是否正确
 * 408	IP与APIKEY没有绑定
 * 200	查无记录
 * 107	同一个样本重复调用次数达到上限
 */
@Data
public class ResponseDTO {
    private int code;
    private String msg;
    private Result data;
    private String serialNumber;

    @Data
    public static class Result {
        private int result;//“1”表示一致，“2”表示不一致
        private String message;
        private int isTrans;//是否携号转网	0：否,1：是
        private int orignOpe;//号段所属运营商	1:移动，2：联通，3：电信
        private int realOpe;//号段实际所属运营商
    }

    public static void main(String[] args) {
        ResponseDTO responseDTO = JSONUtil.toBean("{\n" +
                "    \"code\":\"100\",\n" +
                "    \"msg\":\"查询成功\",\n" +
                "    \"data\":{\n" +
                "        \"result\":\"2\",\n" +
                "        \"message\":\"不一致\",\n" +
                "        \"isTrans\":\"0\",\n" +
                "        \"orignOpe\":\"1\",\n" +
                "        \"realOpe\":\"1\"\n" +
                "    },\n" +
                "    \"serialNumber\":\"20220511173735100018\"\n" +
                "}", ResponseDTO.class);
        System.out.println(responseDTO);
    }
}
//{
//        "code":"100",
//        "msg":"查询成功",
//        "data":{
//        "result":"2",
//        "message":"不一致",
//        "isTrans":"0",
//        "orignOpe":"1",
//        "realOpe":"1"
//        },
//        "serialNumber":"20220511173735100018"
//        }

package com.muling.common.result;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author haoxr
 * @date 2020-06-23
 **/
@AllArgsConstructor
@NoArgsConstructor
public enum ResultCode implements IResultCode, Serializable {

    SUCCESS("00000", "SUCCESS"),
    WARN("00001", "WARN"),
    ERROR("00002","{0}"),

    USER_ERROR("A0001", "用户数据错误."),

    WXOPEN_AUTH_ERROR("A0101", "微信开放平台授权错误."),
    WXOPEN_AUTH_BIND("A0102", "微信开放平台授权绑定."),
    WXOPEN_AUTH_BIND_ALREADY("A0103", "微信开放平台授权已经绑定."),
    WXOPEN_AUTH_BIND_INVALID("A0104", "微信开放平台绑定码失效."),
    WXOPEN_AUTH_BIND_FAILUR("A0105", "微信开放平台绑定失败."),
    ALIPAY_AUTH_ERROR("A0111", "支付宝平台授权错误."),
    ALIPAY_AUTH_BIND("A0112", "支付宝授权绑定."),
    ALIPAY_AUTH_BIND_ALREADY("A0113", "支付宝授权已经绑定."),
    ALIPAY_AUTH_BIND_INVALID("A0114", "支付宝绑定码失效."),
    ALIPAY_AUTH_BIND_FAILUR("A0105", "支付宝绑定失败."),
    USER_AUTH_ERROR("A0199", "用户异常授权."),
    USER_LOGIN_ERROR("A0200", "user login error."),

    USER_NOT_EXIST("A0201", "用户不存在"),
    USER_ACCOUNT_LOCKED("A0202", "账户锁定."),
    USER_ACCOUNT_INVALID("A0203", "账户无效."),
    USER_ALREADY_EXIST("A0204", "用户已存在."),
    USERNAME_ALREADY_EXIST("A0205", "用户名已存在."),
    USER_AUTH_NOT_EXIST("A0208", "用户实名不存在"),

    EMAIL_ALREADY_EXIST("A0209", "email already exist."),
    USERNAME_OR_PASSWORD_ERROR("A0210", "用户名或者密码错误."),
    PASSWORD_ENTER_EXCEED_LIMIT("A0211", "Input error password too much times"),
    CLIENT_AUTHENTICATION_FAILED("A0212", "client authen failed."),
    TRADE_PASSWORD_ERROR("A0213", "交易密码错误."),
    TOKEN_INVALID_OR_EXPIRED("A0230", "token invalid  or expired."),
    TOKEN_ACCESS_FORBIDDEN("A0231", "token is forbidden"),
    GOOGLECODE_ERROR("A0232", "google code error"),
    USER_NOT_ACTIVAT("A0233", "Please activate your account"),
    USER_FORBIDDEN("A0234", "user is forbidden."),
    OLD_PASSWORD_ERROR("A0235", "Current Password entered is incorrect"),
    VERFICATION_INVALID_ERROR("A0236", "Verfication code entered is invalid"),
    DEVICE_EXIST("A0237", "device is exist"),
    USER_INVITE_CODE_EXIST("A0238", "邀请码已存在，请更换"),
    USER_INVITE_CODE_NOT_EXIST("A0239", "邀请码不存在，无法注册"),

    AUTHORIZED_ERROR("A0300", "authorized error."),
    ACCESS_UNAUTHORIZED("A0301", "access  unauthorized"),
    FORBIDDEN_OPERATION("A0302", "禁止操作"),
    AFS_FAIL("A0303", "afs fail"),

    PARAM_ERROR("A0400", "参数错误.{0}"),
    RESOURCE_NOT_FOUND("A0401", "resource not found."),
    REQUEST_FREQUENTLY("A0402", "request frequently."),
    REQUEST_INVALID("A0403", "请求无效.{0}"),
    PARAM_IS_NULL("A0410", "param is null"),
    PARAM_NO_FILE("A0411", "please select  file"),
    VERIFY_CODE_ERROR("A0412", "验证码错误."),
    COUNTRY_MIN_THREE("A0413", "need to select minimum 3 country"),
    LINK_EXPIRED("A0414", "The link has expired."),
    ACCOUNT_ACTIVATION_EXPIRED("A0415", "Account activation link has been expired. "),
    REQUEST_TOO_FAST("A0416", "请求过于频繁，请稍后再试"),

    USER_UPLOAD_FILE_ERROR("A0700", "upload file error."),
    USER_UPLOAD_FILE_TYPE_NOT_MATCH("A0701", "upload file type not match."),
    USER_UPLOAD_FILE_SIZE_EXCEEDS("A0702", "upload file size exceeds."),
    USER_UPLOAD_IMAGE_SIZE_EXCEEDS("A0703", "upload image size exceeds"),
    USER_DOWNLOAD_FILE_ERROR("A0704", "download file error"),

    SYSTEM_EXECUTION_ERROR("B0001", "system run error"),
    SYSTEM_EXECUTION_TIMEOUT("B0102", "system run timeout"),
    SYSTEM_ORDER_PROCESSING_TIMEOUT("B0103", "system order run timeout"),
    DATA_NOT_EXIST("B0104", "数据不存在"),
    ORDER_NO_PAY("B0105", "有未处理的订单，请处理后再提交订单"),
    ITEM_OPEN_NOTHING("B0105", "开出空物品"),
    //已售罄
    ORDER_SELL_OVER("B0106", "已售罄"),
    ORDER_GET_WAITING("B0107", "订单等待返回"),
    SMS_CODE_ERROR("B0108", "获得CODE失败"),
    NO_SUPPORT_PAY("B0109", "系统暂不支持该支付方式"),
    ITEM_ALREADY_BIND("B0110", "绑定物品，不能转移"),
    ITEM_UNSUPPORTED_TRANSFER("B0111", "物品不支持批量转移"),

    SYSTEM_DISASTER_RECOVERY_TRIGGER("B0200", "system disaster recovery trigger."),
    FLOW_LIMITING("B0210", "flow limitting"),
    DEGRADATION("B0220", "degraded"),

    SYSTEM_RESOURCE_ERROR("B0300", "system resource error."),
    SYSTEM_RESOURCE_EXHAUSTION("B0310", "system resource exhaustion"),
    SYSTEM_RESOURCE_ACCESS_ERROR("B0320", "system resource access error."),
    SYSTEM_READ_DISK_FILE_ERROR("B0321", "system read disk error."),

    CALL_THIRD_PARTY_SERVICE_ERROR("C0001", "call third party service error."),
    MIDDLEWARE_SERVICE_ERROR("C0100", "middleware service error."),
    INTERFACE_NOT_EXIST("C0113", "interface not  exist."),

    MESSAGE_SERVICE_ERROR("C0120", "MQ service error."),
    MESSAGE_DELIVERY_ERROR("C0121", "message deliver error."),
    MESSAGE_CONSUMPTION_ERROR("C0122", "message consumer error."),
    MESSAGE_SUBSCRIPTION_ERROR("C0123", "message sub/pub error."),
    MESSAGE_GROUP_NOT_FOUND("C0124", "message  group not found."),

    DATABASE_ERROR("C0300", "database error."),
    DATABASE_TABLE_NOT_EXIST("C0311", "table not exist."),
    DATABASE_COLUMN_NOT_EXIST("C0312", "column not exist."),
    DATABASE_DUPLICATE_COLUMN_NAME("C0321", "duuplicate column name."),
    DATABASE_DEADLOCK("C0331", "database  deadlock."),
    DATABASE_PRIMARY_KEY_CONFLICT("C0341", "pk conflict"),

    BAD_GATEWAY("C0333", "bad gateway."),

    TRANSFER_DISABLED("D0001", "转赠功能不可用."),
    TRANSFER_ITEM_DISABLED("D0002", "转赠物品不可用."),
    TRANSFER_ITEM_CONSUME_TYPE_DIFFERENT("D0003", "转赠物品消耗类型不同，不同一起转赠."),

    MISSION_GROUP_NO_EXIST("M0001", "任务包无法获取."),

    BUSINESS_CHAIN_EXECUTION("F0001", "BlockChain error."),

    WALLET_COIN_TYPE_DISABLED("G0001", "钱包币种已禁用."),
    WALLET_BALANCE_LESS_THAN_ZERO("G0001", "wallet less than zero error."),
    WALLET_BALANCE_NOT_ENOUGH("G0002", "钱包余额不足."),
    WALLET_INPUT_BALANCE_LESS_THAN_ZERO("G0003", "钱包输入金额不能小于0."),
    WALLET_COIN_NOT_EXIST_ERROR("I0004", "wallet coin not exist error."),


    MAIL_MESSAGE_NOT_FOUND_ERROR("J0001", "mail message not found error."),

    EXCE_SCORE_BALANCE("K0001", "score balance.GmsSquad id={0},score={1}"),

    CARD_CONF_NOT_EXIST_ERROR("L0001", "card conf not exist error."),

    LOGIN_AGAIN("X0001", "please login again."),

    ;


    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    private String code;

    private String msg;

    @Override
    public String toString() {
        return "{" +
                "\"code\":\"" + code + '\"' +
                ", \"msg\":\"" + msg + '\"' +
                '}';
    }


    public static ResultCode getValue(String code) {
        for (ResultCode value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return SYSTEM_EXECUTION_ERROR; // 默认系统执行错误
    }
}

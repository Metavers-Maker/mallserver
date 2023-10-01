package com.muling.mall.oms.pojo.form;

import com.muling.mall.oms.enums.PayTypeEnum;
import lombok.Data;

@Data
public class BankBindForm {

    /**
     * 手机号
     */
    private String mobile;

//    /**
//     * 验证码
//     */
//    private String code;

    /**
     * 卡号
     */
    private String cardNo;

    /**
     * 银行名
     */
    private String cardBank;

    /**
     * 卡类型 1借记 2贷记
     */
    private String cardType;

    /**
     * 信用卡背面csv
     */
    private String checkNo;
    /**
     * 过期日期
     */
    private String checkExpiry;
}

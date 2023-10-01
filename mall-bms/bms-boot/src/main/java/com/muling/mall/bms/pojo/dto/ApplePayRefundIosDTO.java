package com.muling.mall.bms.pojo.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "苹果退款通知")
public class ApplePayRefundIosDTO implements Serializable {
    /**
     * App Store Connect 生成的标识符，App Store 使用该标识符来唯一标识用户订阅续订的自动续订订阅。将此值视为 64 位整数
     */
    private String auto_renew_adam_id;

    /**
     * 用户订阅续订的自动续订订阅的产品标识符。
     */
    private String auto_renew_product_id;

    /**
     * 自动续订订阅产品的当前续订状态。请注意，这些值与收据中的值不同。auto_renew_status
     * 可能的值： true, false
     */
    private String auto_renew_status;

    /**
     * 用户打开或关闭自动续订订阅的续订状态的时间，采用类似于 ISO 8601 标准的日期时间格式
     */
    private String auto_renew_status_change_date;

    /**
     * 用户打开或关闭自动续订订阅的续订状态的时间，采用 UNIX 纪元时间格式，以毫秒为单位。使用此时间格式来处理日期
     */
    private String auto_renew_status_change_date_ms;

    /**
     * 用户打开或关闭自动续订订阅的续订状态的时间，以太平洋标准时间表示
     */
    private String auto_renew_status_change_date_pst;

    /**
     * 包含应用程序包 ID 的字符串
     */
    private String bid;

    /**
     * 包含应用程序包版本的字符串
     */
    private String bvrs;

    /**
     * App Store 生成收据的环境。
     * 可能的值： Sandbox, PROD
     */
    private String environment;

    /**
     * 订阅过期的原因。此字段仅适用于过期的自动续订订阅
     */
    private String expiration_intent;

    /**
     * 触发通知的订阅事件
     */
    private String notification_type;

    /**
     * App Store 服务器通知为其发送的原始交易标识符。此字段仅出现时是CONSUMPTION_REQUESTnotification_typeCONSUMPTION_REQUEST
     */
    private long original_transaction_id;

    /**
     * 与您在验证收据时在password字段中提交的共享机密相同的值。requestBody
     */
    private String password;

    /**
     * 一个对象，其中包含有关应用程序最近的应用程序内购买交易的信息
     */
    private UnifiedReceipt unified_receipt;


    @Data
    public static class UnifiedReceipt {
        /**
         * App Store 生成收据的环境。
         * 可能的值： Sandbox, Production
         */
        private String environment;

        /**
         * 最新的 Base64 编码应用收据
         */
        private byte latest_receipt;

        /**
         * 包含解码值的最新 100 笔应用内购买交易的数组。此数组不包括您的应用标记为已完成的消费品的交易。
         * 此数组的内容与用于接收验证的verifyReceipt端点响应中的内容相同。
         * latest_receiptresponseBody.Latest_receipt_info
         */
        private List<LatestReceiptInfo> latest_receipt_info;

        /**
         * 一个数组，其中每个元素都包含 中标识的每个自动续订订阅的挂起续订信息。
         * 此数组的内容与用于接收验证的verifyReceipt端点响应中的内容相同。
         * product_idresponseBody.Pending_renewal_info
         */
        private List<PendingRenewalInfo> pending_renewal_info;
    }

    @Data
    public class LatestReceiptInfo {
        /**
         * 在与此交易相关联。仅当您的应用程序在用户购买时提供了该字段时，才会出现此字段；它只存在于沙盒环境中。appAccountTokenappAccountToken(_:)
         */
        private String app_account_token;

        /**
         * App Store 以类似于 ISO 8601 的日期时间格式退款或撤销交易的时间。此字段仅适用于退款或撤销的交易
         */
        private String cancellation_date;

        /**
         * App Store 退还交易或从家庭共享中撤销交易的时间，以 UNIX 纪元时间格式，以毫秒为单位。此字段仅适用于已退款或已撤销的交易。
         * 使用此时间格式处理日期。有关更多信息，请参阅 。cancellation_date_ms
         */
        private String cancellation_date_ms;

        /**
         * App Store 退款或取消家庭共享的时间，以太平洋标准时间为准。此字段仅适用于已退款或已撤销的交易
         */
        private String cancellation_date_pst;

        /**
         * 退款或撤销交易的原因。值“1” 表示客户由于您的应用程序中的实际或感知问题取消了他们的交易。值“0” 表示交易因其他原因被取消；例如，如果客户不小心进行了购买。
         * 可能的值： 1, 0
         */
        private String cancellation_reason;

        /**
         * 订阅到期或续订的时间，以 UNIX 纪元时间格式，以毫秒为单位。使用此时间格式处理日期。请注意，此字段在收据中调用。expires_date_ms
         */
        private String expires_date;

        /**
         * 订阅到期或续订的时间，以 UNIX 纪元时间格式，以毫秒为单位。使用此时间格式处理日期。有关更多信息，请参阅。expires_date_ms
         */
        private String expires_date_ms;

        /**
         * 订阅到期或续订的时间，以太平洋标准时间表
         */
        private String expires_date_pst;

        /**
         * 一个值，指示用户是产品的购买者，还是可以通过家庭共享访问产品的家庭成员。有关更多信息，请参阅。in_app_ownership_type
         * 可能的值： FAMILY_SHARED, PURCHASED
         */
        private String in_app_ownership_type;

        /**
         * 自动续订订阅是否处于介绍价格期的指标。有关更多信息，请参阅。is_in_intro_offer_period
         * 可能的值： true, false
         */
        private String is_in_intro_offer_period;

        /**
         * 订阅是否处于免费试用期的指标。有关更多信息，请参阅。is_trial_period
         * 可能的值： true, false
         */
        private String is_trial_period;

        /**
         * 由于用户升级，系统取消订阅的指示符。此字段仅适用于升级事务。
         * 价值： true
         */
        private String is_upgraded;

        /**
         * 您在 App Store Connect 中配置的订阅优惠的参考名称。当客户兑换订阅优惠代码时，会出现此字段。有关更多信息，请参阅。offer_code_ref_name
         */
        private String offer_code_ref_name;

        /**
         * 原始应用购买的时间，采用类似于 ISO 8601 标准的日期时间格式
         */
        private String original_purchase_date;

        /**
         * 原始应用购买的时间，以 UNIX 纪元时间格式，以毫秒为单位。使用此时间格式处理日期。此值表示订阅的初始购买日期。
         * 原始购买日期适用于所有产品类型，并在同一产品 ID 的所有交易中保持不变。该值对应于 StoreKit 中原始事务的属性。transactionDate
         */
        private String original_purchase_date_ms;

        /**
         * 原始应用购买的时间，采用太平洋标准时间
         */
        private String original_purchase_date_pst;

        /**
         * 原始购买的交易标识符。有关更多信息，请参阅。original_transaction_id
         */
        private String original_transaction_id;

        /**
         * 用户兑换的订阅优惠的标识符。有关更多信息，请参阅。promotional_offer_id
         */
        private String promotional_offer_id;

        /**
         * 所购买产品的唯一标识符。您在 App Store Connect 中创建产品时提供此值，它对应于存储在事务属性中的对象的属性。productIdentifierSKPaymentpayment
         */
        private String product_id;

        /**
         * App Store 以类似于 ISO 8601 标准的日期时间格式向用户帐户收取订阅购买或续订费用的时间
         */
        private String purchase_date;

        /**
         * App Store 向用户帐户收取订阅购买或过期后续费的时间，采用 UNIX 纪元时间格式，以毫秒为单位。使用此时间格式处理日期
         */
        private String purchase_date_ms;

        /**
         * App Store 向用户帐户收取订阅购买或过期后续订费用的时间，以太平洋标准时间计算
         */
        private String purchase_date_pst;

        /**
         * 购买的消耗品数量。该值对应于存储在事务quantity属性中的SKPayment对象的payment属性。“1”除非使用可变付款修改，否则该值通常为。最大值为“10”
         */
        private String quantity;

        /**
         * 订阅所属的订阅组标识。此字段的值与 中的属性相同。subscriptionGroupIdentifierSKProduct
         */
        private String subscription_group_identifier;

        /**
         * 交易的唯一标识符，例如购买、恢复或续订。有关更多信息，请参阅。transaction_id
         */
        private String transaction_id;

        /**
         * 跨设备购买事件的唯一标识符，包括订阅续订事件。该值是识别订阅购买的主键
         */
        private String web_order_line_item_id;

    }

    @Data
    public class PendingRenewalInfo {
        /**
         * 自动续订订阅的当前续订首选项。此键的值对应于客户订阅续订的产品的属性。productIdentifier
         */
        private String auto_renew_product_id;

        /**
         * 自动续订订阅的当前续订状态。有关更多信息，请参阅。auto_renew_status
         * 可能的值： 1, 0
         */
        private String auto_renew_status;

        /**
         * 订阅过期的原因。此字段仅适用于包含过期、自动更新订阅的收据。有关更多信息，请参阅。expiration_intent
         * 可能的值： 1, 2, 3, 4, 5
         */
        private String expiration_intent;

        /**
         * 订阅续订宽限期到期的时间，采用类似于 ISO 8601 的日期时间格式
         */
        private String grace_period_expires_date;

        /**
         * 订阅续订宽限期到期的时间，采用 UNIX 纪元时间格式，以毫秒为单位。此密钥仅适用于启用了计费宽限期的应用程序以及用户在续订时遇到计费错误时。使用此时间格式处理日期
         */
        private String grace_period_expires_date_ms;

        /**
         * 订阅续订宽限期到期的时间，在太平洋时区
         */
        private String grace_period_expires_date_pst;

        /**
         * 指示 Apple 正在尝试自动续订过期订阅的标志。此字段仅在自动续订订阅处于计费重试状态时出现。有关更多信息，请参阅。is_in_billing_retry_period
         * 可能的值： 1, 0
         */
        private String is_in_billing_retry_period;

        /**
         * 您在 App Store Connect 中配置的订阅优惠的参考名称。当客户兑换订阅优惠代码时，会出现此字段。有关更多信息，请参阅。offer_code_ref_name
         */
        private String offer_code_ref_name;

        /**
         * 原始购买的交易标识符
         */
        private String original_transaction_id;

        /**
         * 订阅价格上涨的价格同意状态。仅当 App Store 通知客户价格上涨时，才会出现此字段。如果客户同意，默认值为"0"和 更改为"1" 。
         * 可能的值： 1, 0
         */
        private String price_consent_status;

        /**
         * 所购买产品的唯一标识符。您在 App Store Connect 中创建产品时提供此值，它对应于存储在事务属性中的对象的属性。productIdentifierSKPaymentpayment
         */
        private String product_id;

        /**
         * 用户兑换的自动续订订阅的促销优惠的标识符。在 App Store Connect 中创建促销优惠时，您在促销优惠标识符字段中提供此值。有关更多信息，请参阅。promotional_offer_id
         */
        private String promotional_offer_id;

    }
}

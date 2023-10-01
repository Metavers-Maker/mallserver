package com.muling.mall.oms.util;

import lombok.Data;

import java.util.List;

@Data
public class ApplePayCallBackResponse {
    private Integer status;
    private String environment;
    private Receipt receipt;

    @Data
    public static class Receipt {
        private String receipt_type;
        private Integer adam_id;
        private Integer app_item_id;
        private String bundle_id;
        private String application_version;
        private Long download_id;
        private Integer version_external_identifier;
        private String receipt_creation_date;
        private String receipt_creation_date_ms;
        private String receipt_creation_date_pst;
        private String request_date;
        private String request_date_ms;
        private String request_date_pst;
        private String original_purchase_date;
        private String original_purchase_date_ms;
        private String original_purchase_date_pst;
        private String original_application_version;
        private List<InApp> in_app;
    }

    @Data
    public static class InApp {
        private String quantity;
        private String product_id;
        private String transaction_id;
        private String original_transaction_id;
        private String purchase_date;
        private String purchase_date_ms;
        private String purchase_date_pst;
        private String original_purchase_date;
        private String original_purchase_date_ms;
        private String original_purchase_date_pst;
        private Boolean is_trial_period;
        private String in_app_ownership_type;
    }
}

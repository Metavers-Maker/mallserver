package com.muling.common.constant;

public interface RedisConstants {

    String BUSINESS_NO_PREFIX = "business_no:";

    public static final String FOLLOWER = "relation:follower_";
    public static final String FOLLOWING = "relation:following_";


    public static final String UMS_AUTH_SUFFIX = "ums:auth:";
    /**
     * OMS 相关缓存
     * */
    public static final String OMS_ITEM_NO_SPU_PREFIX = "oms:item-no:spu:";
    public static final String OMS_ORDER_WITHOUT_PAY_SUFFIX = "oms:order:no-pay:";


    /**
     * PMS 相关缓存
     * */
    public static final String PMS_SPU_START_PREFIX = "pms:spu:start:";

    public static final String PMS_SKU_STOCK_PREFIX = "pms:sku:stock:";
    public static final String PMS_SKU_RND_STOCK_PREFIX = "pms:sku:stock:rnd:";

    public static final String PMS_SKU_START_PREFIX = "pms:sku:start:";

    String HOT_SEARCH_KEYS = "searches:hot:keys";

}

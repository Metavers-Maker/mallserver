package com.muling.mall.oms.constant;

public class OmsConstants {

    public static final String CART_PREFIX = "cart:";

    public static final String ORDER_TOKEN_PREFIX = "order:token:";

    public static final String ORDER_SN_PREFIX = "order:sn:";

    public static final String ORDER_ID_PREFIX = "order:id:";

    public static final Integer ORDER_TOKEN_EXPIRE_TIME = 3 * 60;

    public static final String ORDER_SECSKILL_PREFIX = "order:sec-skill:";


    public static final String PAY_CALLBACK_PREFIX = "pay:callback:";

    /**
     * 释放锁lua脚本
     */
    public static final String RELEASE_LOCK_LUA_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
}

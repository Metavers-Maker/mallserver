package com.muling.mall.bms.constant;

public class OmsConstants {

    public static final String LVALUE_PUBLISH_PREFIX = "lvalue:publish";

    public static final String CART_PREFIX = "cart:";

    public static final String ORDER_TOKEN_PREFIX = "order:token:";

    public static final String ORDER_SN_PREFIX = "order:sn:";

    public static final String ORDER_ID_PREFIX = "order:id:";

    public static final String ORDER_SN_ITEMS_PREFIX = "order:sn:items:";

    public static final String ITEM_OPEN_PREFIX = "item:open:";

    public static final String ITEM_OP_PREFIX = "item:op:";

    public static final String ITEM_TRANSFER_PREFIX = "item:transfer:";
    public static final String ITEM_BSN_TRANSFER_PREFIX = "item:bsn:transfer:";

    public static final String ITEM_BSN_AUTO_QUERY_PREFIX = "item:bsn:auto:query:";
    public static final String ITEM_BSN_AUTO_TRANS_PREFIX = "item:bsn:auto:trans:";

    public static final String ITEM_BSN_TRANS_ING_PREFIX = "item:bsn:trans:ing:";

    public static final String ITEM_PUBLISH_PREFIX = "item:publish:";

    public static final String ITEM_PUBLISH_BUY_PREFIX = "item:publish:buy:";

    public static final String ITEM_MINT_PREFIX = "item:mint:";

    public static final String ITEM_MINT_BATCH_PREFIX = "item:mint:batch:";
    public static final String ITEM_MINT_AUTO_QUERY_PREFIX = "item:mint:auto:query:";

    public static final String ITEM_AIRDROP_PREFIX = "item:airdrop:";

    public static final String ITEM_COMPOUND_PREFIX = "item:compound:";

    public static final String ITEM_EXCHANGE_PREFIX = "item:exchange:";

    public static final String EXCHANGE_ITEM_NUM_PREFIX = "exchange:item:";

    public static final String EXCHANGE_MAX_LIMIT_PREFIX = "exchange:max:limit:";

    public static final String ITEM_MARKET_PREFIX = "item:market:";

    public static final String ITEM_STAKE_PREFIX = "item:stake:";

    public static final String ITEM_DISPATCH_PREFIX = "item:dispatch:";

    public static final String ITEM_CLAIM_PREFIX = "item:claim:";

    public static final Integer ORDER_TOKEN_EXPIRE_TIME = 15 * 60;

    public static final String ORDER_SECSKILL_PREFIX = "order:sec-skill:";


    public static final String PAY_CALLBACK_PREFIX = "pay:callback:";

    public static final String MISSION_APPLY_PREFIX = "mission:apply:";

    public static final String MISSION_CHECK_PREFIX = "mission:check:";

    public static final String MISSION_APPLYGROUP_PREFIX = "mission:apply-group:";


    /**
     * 释放锁lua脚本
     */
    public static final String RELEASE_LOCK_LUA_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
}

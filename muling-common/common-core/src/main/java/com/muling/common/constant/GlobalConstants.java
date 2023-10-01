package com.muling.common.constant;

/**
 * 全局常量
 *
 * @author <a href="mailto:xianrui0365@163.com">xianrui</a>
 * @date 2021/10/30 9:32
 */
public interface GlobalConstants {

    public static final Integer STATUS_YES = 1;
    public static final Integer STATUS_NO = 0;

    String ROOT_ROLE_CODE = "ROOT";

    String URL_PERM_ROLES_KEY = "permission:url:";
    String BTN_PERM_ROLES_KEY = "permission:btn:";


    public static final String MQ_AUTO_LOG_QUEUE = "autolog.queue";
    public static final String MQ_AUTO_LOG_EXCHANGE = "autolog.exchange";
    public static final String MQ_AUTO_LOG_KEY = "autolog.routing.key";


    public static final String MQ_MEMBER_AUTH_QUEUE = "member.auth.queue";
    public static final String MQ_MEMBER_AUTH_EXCHANGE = "member.auth.exchange";
    public static final String MQ_MEMBER_AUTH_KEY = "member.auth.routing.key";

    public static final String MQ_MEMBER_AUTH_SUCCESS_QUEUE = "member.auth.success.queue";
    public static final String MQ_MEMBER_AUTH_SUCCESS_EXCHANGE = "member.auth.success.exchange";
    public static final String MQ_MEMBER_AUTH_SUCCESS_KEY = "member.auth.success.routing.key";


    public static final String MQ_ORDER_NOTIFY_QUEUE = "order.notify.queue";
    public static final String MQ_ORDER_NOTIFY_EXCHANGE = "order.notify.exchange";
    public static final String MQ_ORDER_NOTIFY_KEY = "order.notify.routing.key";


    public static final String MQ_ORDER_SECKILL_QUEUE = "order.seckill.queue";
    public static final String MQ_ORDER_SECKILL_EXCHANGE = "order.seckill.exchange";
    public static final String MQ_ORDER_SECKILL_KEY = "order.seckill.routing.key";

    public static final String MQ_ORDER_CREATE_EXCHANGE = "order.exchange";
    public static final String MQ_ORDER_CREATE_KEY = "order.create.routing.key";

    public static final String MQ_ORDER_PAY_SUCCESS_QUEUE = "order.pay.success.queue";
    public static final String MQ_ORDER_PAY_SUCCESS_EXCHANGE = "order.pay.success.exchange";
    public static final String MQ_ORDER_PAY_SUCCESS_KEY = "order.pay.success.routing.key";

    public static final String MQ_ITEM_TRANS_SUCCESS_QUENE = "item.trans.success.queue";
    public static final String MQ_ITEM_TRANS_SUCCESS_EXCHANGE = "item.trans.success.exchange";
    public static final String MQ_ITEM_TRANS_SUCCESS_KEY = "item.trans.success.routing.key";

    public static final String MQ_ITEM_BUY1_SUCCESS_QUENE = "item.buy1.success.queue";
    public static final String MQ_ITEM_BUY1_SUCCESS_EXCHANGE = "item.buy1.success.exchange";
    public static final String MQ_ITEM_BUY1_SUCCESS_KEY = "item.buy1.success.routing.key";

    public static final String MQ_ITEM_CHAIN_MINT_SUCCESS_QUENE = "item.chain.mint.success.queue";
    public static final String MQ_ITEM_CHAIN_MINT_SUCCESS_EXCHANGE = "item.chain.mint.success.exchange";
    public static final String MQ_ITEM_CHAIN_MINT_SUCCESS_KEY = "item.chain.mint.success.routing.key";
    public static final String MQ_ITEM_CHAIN_TRANS_SUCCESS_QUENE = "item.chain.trans.success.queue";
    public static final String MQ_ITEM_CHAIN_TRANS_SUCCESS_EXCHANGE = "item.chain.trans.success.exchange";
    public static final String MQ_ITEM_CHAIN_TRANS_SUCCESS_KEY = "item.chain.trans.success.routing.key";

    public static final String MQ_MEMBER_INVITE_QUEUE = "member.invite.queue";
    public static final String MQ_MEMBER_INVITE_EXCHANGE = "member.invite.exchange";
    public static final String MQ_MEMBER_INVITE_KEY = "member.invite.routing.key";

    public static final String MQ_MEMBER_LOG_QUEUE = "member.log.queue";
    public static final String MQ_MEMBER_LOG_EXCHANGE = "member.log.exchange";
    public static final String MQ_MEMBER_LOG_KEY = "member.log.routing.key";

    public static final String MQ_MEMBER_REWARD_QUEUE = "member.reward.queue";
    public static final String MQ_MEMBER_REWARD_EXCHANGE = "member.reward.exchange";
    public static final String MQ_MEMBER_REWARD_KEY = "member.reward.routing.key";

    public static final String MQ_APPLE_PAY_QUEUE = "order.apply-pay.queue";
    public static final String MQ_APPLE_PAY_EXCHANGE = "order.apply-pay.exchange";
    public static final String MQ_APPLE_PAY_KEY = "order.apply-pay.routing.key";

    public static final String MQ_FARM_SETTLE_QUEUE = "order.farm-settle.queue";
    public static final String MQ_FARM_SETTLE_EXCHANGE = "order.farm-settle.exchange";
    public static final String MQ_FARM_SETTLE_KEY = "order.farm-settle.routing.key";


    public static final String MQ_MEMBER_REGISTER_QUEUE = "member.register.queue";

    public static final String MQ_OH_MEMBER_REGISTER_QUEUE = "member.oh.register.queue";
    public static final String MQ_MEMBER_REGISTER_EXCHANGE = "member.register.exchange";
    public static final String MQ_MEMBER_REGISTER_KEY = "member.register.routing.key";

    public static final String MQ_ACTIVE_VALUE_QUEUE = "active.value.queue";
    public static final String MQ_ACTIVE_VALUE_EXCHANGE = "active.value.exchange";
    public static final String MQ_ACTIVE_VALUE_KEY = "active.value.routing.key";

    public static final String MQ_TEAM_ACTIVE_VALUE_QUEUE = "team.active.value.queue";
    public static final String MQ_TEAM_ACTIVE_VALUE_EXCHANGE = "team.active.value.exchange";
    public static final String MQ_TEAM_ACTIVE_VALUE_KEY = "team.active.value.routing.key";


    public static final String MQ_TASK_CHECK_SUCCESS_QUEUE = "task.check.success.queue";
    public static final String MQ_TASK_CHECK_SUCCESS_EXCHANGE = "task.check.success.exchange";
    public static final String MQ_TASK_CHECK_SUCCESS_KEY = "task.check.success.routing.key";

    public static final String[] MOBILE_LOGIN_WHITE_LIST = {};

    public static final Long ADMIN_MEMBER_ID = 1L;
}

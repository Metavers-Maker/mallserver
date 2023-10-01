package com.muling.mall.wms.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 钱包操作类型枚举
 *
 * @author <a href="mailto:xianrui0365@163.com">haoxr</a>
 */
public enum WalletOpTypeEnum implements IBaseEnum<Integer> {

    ADMIN(0, "管理发放"),
    ACTIVITY(1, "活动"),
    OPEN(2, "盲盒"),
    INVITE_TO_REGISTER_REWARD(3, "邀请注册"),
    REGISTER_REWARD(4, "注册"),
    INVITE_TO_AUTH_REWARD(5, "邀请认证奖励"),
    AUTH_REWARD(6, "认证奖励"),
    TRANSFER_CONSUME(7, "转赠消耗"),
    COMPOUND_CONSUME(8, "合成消耗"),
    EXCHANGE_CONSUME(9, "兑换消耗"),
    TRANSFER(10, "转赠"),
    TRANSFER_RECEIVE(11, "转赠接收"),
    MARKET_BUY_CONSUME(12, "市场购买消耗"),
    EXCHANGE_RECEIVE(13, "兑换接收"),
    STAKE_CLAIM(14, "质押奖励"),
    MARKET_PENDING_CONSUME(15, "市场挂单消耗"),
    FARM_BAG_BUY_REWARD(16, "工作包购买奖励"),
    FARM_BAG_CLAIM_REWARD(17, "工作包领取奖励"),
    FARM_BAG_ACTIVATE_CONSUME(18, "工作包激活消耗"),
    FARM_BAG_CLOSE_CONSUME(19, "工作包关闭减扣"),
    FARM_BAG_RAKE_BACK_CLAIM_REWARD(20, "工作包返佣领取奖励"),
    TEAM_START_REWARD(21, "星级达人奖励"),
    MISSION_COMPLETE_REWARD(22, "任务完成奖励"),
    MISSION_COMPLETE_COST(23, "任务领取消耗"),
    MISSION_GROUP_COMPLETE_REWARD(24, "任务包完成奖励"),
    MISSION_SWAP_ADD_REWARD(25, "交易所加操作"),
    MISSION_SWAP_DEC_REWARD(26, "交易所减操作"),
    SWAP_CONSUME(27, "币币兑换消耗"),
    SWAP_RECEIVE(28, "币币兑换接收"),
    MARKET_CREATE(29, "市场寄售"),
    MARKET_CLOSE(30, "市场关闭"),
    MARKET_BUY(31, "市场购买"),
    MARKET_SELL_FREEZE(32, "积分市场扣除"),
    MARKET_SELL_REBACK(33, "积分市场返还"),
    MARKET_SELL_CONFIRM(34, "积分市场获取"),
    MARKET_SELL_REWARD(35, "购买奖励"),
    TEAM_STAR_FEE_REWARD(36, "星级分润"),
    TEAM_AD_VIDEO_REWARD(37, "激励视频奖励"),
    //
    TEAM_GAME_DUOMOB_REWARD(38, "游戏奖励"),
    WITHDRAW_FREEZE(39, "积分提现冻结"),
    WITHDRAW_UNFREEZE(40, "积分提现解冻"),
    WITHDRAW_COMPLETE(41, "积分提现完成"),
    WITHDRAW_REJECT(42, "系统提现拒绝"),
    SYS_RAKE_MODIFY(43, "系统返佣修正扣除"),
    OH_BATTLE_REWARD(100, "OH Battle Reward"),
    OH_BATTLE_EXIT_REWARD(101, "OH Battle Exit Reward"),
    OH_BATTLE_SUCCESS_REWARD(102, "OH Battle Success Reward"),
    OH_TASK_EVERYDAY(200, "OH Task Everyday"),
    OH_TASK_COMBINE(201, "OH Task Combine"),

    OH_TASK_NEW_PLAYER(202, "OH Task New Player"),
    OH_BANK_UPLEVEL(301, "OH Bank Uplevel"),
    OH_BANK_CLAIM(302, "OH Bank Claim"),
    OH_PRICE_SWAP(303, "OH Price Swap"),
    STICK_REWARD(401, "持仓奖励"),
    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    WalletOpTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}

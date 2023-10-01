package com.muling.mall.bms.pojo.vo.app;

import com.muling.mall.bms.enums.FromTypeEnum;
import lombok.Data;


@Data
public class MemberItemVO {

    private Long id;

    /**
     * 用户id
     */
    private Long memberId;

    private Long spuId;

    private Integer type;

    private String hexId;

    private String itemNo;

    private String name;

    private String contract;

    private String picUrl;

    private String sourceUrl;

    private Integer sourceType;

    /**
     * 铸造状态
     */
    private Integer status;

    /**
     * 冻结
     */
    private Integer freeze;

    /**
     * 冻结类型
     */
    private Integer freezeType;

    /**
     * 藏品来源类型
     */
    private FromTypeEnum fromType;

    /**
     * 上次变动价格
     */
    private Long swapPrice;

    /**
     * 上链操作的ID
     */
    private String operationId;

    /**
     * 持仓时间
     */
    private Integer stickNum;

    /**
     * 奖励时间
     */
    private Long started;

    private Long updated;
}

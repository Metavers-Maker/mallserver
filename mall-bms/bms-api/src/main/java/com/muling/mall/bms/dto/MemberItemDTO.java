package com.muling.mall.bms.dto;

import lombok.Data;

@Data
public class MemberItemDTO {

    private Long id;

    /**
     * 用户id
     */
    private Long memberId;

    /**
     * SPUid
     */
    private Long spuId;

    /**
     * 物品类型
     */
    private Integer type;

    /**
     * 物品NFT-ID
     */
    private String hexId;

    /**
     * 物品编号
     */
    private String itemNo;

    /**
     * 物品名称
     */
    private String name;

    /**
     * 封面
     */
    private String picUrl;

    /**
     * 资源图
     */
    private String sourceUrl;
}

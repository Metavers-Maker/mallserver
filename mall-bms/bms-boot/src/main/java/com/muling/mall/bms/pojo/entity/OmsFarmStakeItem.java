package com.muling.mall.bms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 锁仓详情
 */
@Data
@Accessors(chain = true)
public class OmsFarmStakeItem extends BaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long stakeMemberId;

    private Long poolId;

    private Long memberId;

    private Long itemId;

    private Long spuId;

    private String itemName;

    private String itemNo;

    private String picUrl;

    private Double allocPoint;

    private Integer days;

    private Integer currentDays;

//    @JsonInclude(value = JsonInclude.Include.NON_NULL)
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime unlockedTime;
}

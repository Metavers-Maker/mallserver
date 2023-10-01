package com.muling.mall.bms.pojo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.muling.mall.bms.enums.*;
import com.muling.mall.pms.enums.BindEnum;
import com.muling.mall.pms.enums.InsideEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class MemberItemExportDTO {

    /**
     * 用户id
     */
    private Long memberId;

    /**
     * 用户昵称
     * */
    private String memberNickName;

    /**
     * 用户手机号
     * */
    private String memberMobile;

    /**
     * 藏品ID
     * */
    private Long spuId;

    /**
     * 藏品名称 name
     * */
    private String name;

    /**
     * 藏品sku名称
     * */
    private Long skuName;

    /**
     * 藏品编号
     * */
    private String itemNo;

    /**
     * 转入时间
     * */
    private LocalDateTime transferTime;

    /**
     * 转入价格
     * */
    private Long transferPrice;

    /**
     * 首发价格
     * */
    private Long firstPrice;

    /**
     * 藏品来源类型
     */
    private String fromType;
}

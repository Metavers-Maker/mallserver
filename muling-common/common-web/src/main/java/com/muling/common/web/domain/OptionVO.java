package com.muling.common.web.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 下拉选项对象
 *
 * @author haoxr
 * @date 2022/1/22
 */
@ApiModel("下拉选项对象")
@Data
@NoArgsConstructor
public class OptionVO<T> {

    public OptionVO(T value, String label) {
        this.value = value;
        this.label = label;
    }

    public OptionVO(T value, String label, List<OptionVO> children) {
        this.value = value;
        this.label = label;
        this.children = children;
    }

    @ApiModelProperty("选项的值")
    private T value;

    @ApiModelProperty("选项的标签")
    private String label;

    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private List<OptionVO> children;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @ApiModelProperty("是否禁用该选项，默认false")
    public Boolean disabled;

}

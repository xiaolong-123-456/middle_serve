package com.ruoyi.operate.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
public class RelevanceDTO implements Serializable
{
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "支付通道id")
    private Long payChaId;
    @ApiModelProperty(value = "支付通道编码")
    private String payChaCode;
    @ApiModelProperty(value = "支付通道名称")
    private String payChaName;
    @ApiModelProperty(value = "支付通道类型")
    private String chaType;
    @ApiModelProperty(value = "通道标识id")
    private Long chaLabId;
    @ApiModelProperty(value = "通道标识名称")
    private String chaLabName;
    @ApiModelProperty(value = "通道费率")
    private BigDecimal chaRate;
    @ApiModelProperty(value = "权重")
    private Integer sort;
    @ApiModelProperty(value = "风控id")
    private Long riskControlId;
}

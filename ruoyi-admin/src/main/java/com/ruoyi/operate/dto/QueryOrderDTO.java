package com.ruoyi.operate.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
public class QueryOrderDTO implements Serializable
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户id")
    private Long mchId;
    @ApiModelProperty(value = "商户订单号")
    private String mchOrderNo;
    @ApiModelProperty(value = "验签")
    private String sign;
}

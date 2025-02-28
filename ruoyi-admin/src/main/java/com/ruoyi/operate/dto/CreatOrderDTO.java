package com.ruoyi.operate.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
public class CreatOrderDTO implements Serializable
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户id")
    private Long mchId;
    @ApiModelProperty(value = "产品id")
    private Long productId;
    @ApiModelProperty(value = "商户订单号")
    private String mchOrderNo;
    @ApiModelProperty(value = "支付金额")
    private BigDecimal amount;
    @ApiModelProperty(value = "支付结果后台回调URL")
    private String notifyUrl;
    @ApiModelProperty(value = "支付成功后跳转地址")
    private String returnUrl;
    @ApiModelProperty(value = "验签")
    private String sign;
}

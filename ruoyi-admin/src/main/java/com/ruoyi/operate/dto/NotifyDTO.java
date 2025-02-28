package com.ruoyi.operate.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
public class NotifyDTO implements Serializable
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户id")
    private Long mchId;
    @ApiModelProperty(value = "产品id")
    private Long productId;
    @ApiModelProperty(value = "商户订单号")
    private String mchOrderNo;
    @ApiModelProperty(value = "本渠道订单号")
    private String payOrderId;
    @ApiModelProperty(value = "支付金额")
    private BigDecimal amount;
    @ApiModelProperty(value = "状态（0-订单生成,1-支付中,2-支付成功,3-业务处理完成）")
    private String status;
    @ApiModelProperty(value = "支付成功时间")
    private Long paySuccTime;
    @ApiModelProperty(value = "验签")
    private String sign;
}

package com.ruoyi.operate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;


@Data
public class OperateBillDTO implements Serializable
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户id")
    private Long mchId;
    @ApiModelProperty(value = "商户名称")
    private String mchName;
    @ApiModelProperty(value = "产品id")
    private Long productId;
    @ApiModelProperty(value = "产品名称")
    private String productName;
    @ApiModelProperty(value = "通道标识id")
    private Long chaLabId;
    @ApiModelProperty(value = "通道标识名称")
    private String chaLabName;
    @ApiModelProperty(value = "通道标识代码")
    private String chaLabCode;
    @ApiModelProperty(value = "支付通道id")
    private Long payChaId;
    @ApiModelProperty(value = "支付通道名称")
    private String payChaName;
    @ApiModelProperty(value = "接口代码")
    private String portCode;
    @ApiModelProperty(value = "费率")
    private String rate;
    @ApiModelProperty(value = "交易量")
    private BigDecimal billTotalAmount;
    @ApiModelProperty(value = "入账")
    private BigDecimal incomeAmount;
    @ApiModelProperty(value = "下发")
    private BigDecimal expendAmount;
    @ApiModelProperty(value = "差额")
    private BigDecimal balance;
    @ApiModelProperty(value = "商户收入")
    private BigDecimal mchAmount;
    @ApiModelProperty(value = "代理商收入")
    private BigDecimal agentAmount;
    @ApiModelProperty(value = "平台收入")
    private BigDecimal profit;
    @ApiModelProperty(value = "通道费用")
    private BigDecimal payChaCost;
    @ApiModelProperty(value = "时间校验")
    private String checkDate;
    @ApiModelProperty(value = "订单数")
    private Integer orderNumber;
    @ApiModelProperty(value = "交易成功笔数")
    private Integer orderSuccessNumber;
    @ApiModelProperty(value = "成功率")
    private String successRate;
    @ApiModelProperty(value = "参数")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String beginTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;
}

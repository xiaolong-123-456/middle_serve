package com.ruoyi.operate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Data
public class OperateRiskControlDTO implements Serializable
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "通道id")
    private Long payChaId;
    @ApiModelProperty(value = "当天交易总金额")
    private BigDecimal totalAmount;
    @ApiModelProperty(value = "单笔最大金额")
    private BigDecimal maxAmount;
    @ApiModelProperty(value = "单笔最小金额")
    private BigDecimal minAmount;
    @ApiModelProperty(value = "金额类型(0连续任意金额 1固定金额)")
    private String amountType;
    @ApiModelProperty(value = "固定金额")
    private String fixedAmount;
    @ApiModelProperty(value = "交易开始时间")
    private String startTime;
    @ApiModelProperty(value = "交易结束时间")
    private String endTime;

    @ApiModelProperty(value = "状态(0=正常,1=停用)")
    private String status;
    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    private String delFlag;
    @ApiModelProperty(value = "创建者")
    private String createBy;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @ApiModelProperty(value = "更新者")
    private String updateBy;
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}

package com.ruoyi.operate.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
public class OperatePayProjectInfoDTO implements Serializable
{
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "通道ID")
    private Long id;
    @ApiModelProperty(value = "产品详情ID")
    private Long payProjectInfoId;
    @ApiModelProperty(value = "通道名称")
    private String chaName;
    @ApiModelProperty(value = "费率")
    private String chaRate;
    @ApiModelProperty(value = "权重")
    private Integer sort;
    @ApiModelProperty(value = "风控id")
    private Long riskControlId;
    @ApiModelProperty(value = "风控描述")
    private String riskInfo;
    @ApiModelProperty(value = "产品id")
    private Long payProductId;
    @ApiModelProperty(value = "状态")
    private String status;
    @ApiModelProperty(value = "通道标识名称")
    private String chaLabName;
    @ApiModelProperty(value = "通道编码")
    private String chaCode;
}

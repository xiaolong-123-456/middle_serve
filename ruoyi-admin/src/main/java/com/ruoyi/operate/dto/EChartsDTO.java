package com.ruoyi.operate.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
public class EChartsDTO implements Serializable
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "日期")
    private String dateCollect;
    @ApiModelProperty(value = "总金额")
    private String amount;
}

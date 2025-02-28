package com.ruoyi.operate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Data
public class MchAccountDTO implements Serializable
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户id")
    private Long mchId;
    @ApiModelProperty(value = "商户名称")
    private String mchName;
    @ApiModelProperty(value = "当日账户余额")
    private BigDecimal todayBalance;
    @ApiModelProperty(value = "目前账户余额")
    private BigDecimal balance;
    @ApiModelProperty(value = "更改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}

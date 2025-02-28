package com.ruoyi.operate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Data
public class OperatePayProjectDTO implements Serializable
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "支付产品名称")
    private String proName;
    @ApiModelProperty(value = "商户费率")
    private BigDecimal mchRate;
    @ApiModelProperty(value = "代理商费率")
    private BigDecimal agentRate;
    @ApiModelProperty(value = "接口类型(0单独 1轮询)")
    private String portType;
    @ApiModelProperty(value = "接口类型(0全部  代表上游)")
    private Long chaLabId;
    @ApiModelProperty(value = "支付通道集合")
    private List<OperatePayProjectInfoDTO> selectPayChaList;
    @ApiModelProperty(value = "商户集合")
    private List<Long> mchList;
    @ApiModelProperty(value = "商户Id(单独配置的时候使用)")
    private Long mchId;
    @ApiModelProperty(value = "配置类型(用于关联商户 0全部配置 1单独配置)")
    private String configType;
    @ApiModelProperty(value = "系统类型(0me 1mch 2agent)")
    private String systemType;
    @ApiModelProperty(value = "用户id")
    private String userId;

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

package com.ruoyi.operate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class RobatDTO implements Serializable
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "用户id")
    private Long userId;
    @ApiModelProperty(value = "机器人管理群ID")
    private Long tgmRobat;
    @ApiModelProperty(value = "四方技术群ID")
    private Long tgmTechnology;
    @ApiModelProperty(value = "点数警告（元）")
    private String pointsWarn;
    @ApiModelProperty(value = "通道额度警告（元）")
    private String chaQuotaWarn;
    @ApiModelProperty(value = "警告附加讯息")
    private String warnAppend;
    @ApiModelProperty(value = "拉单异常通知(0开 1关)")
    private String orderLose;
    @ApiModelProperty(value = "商户回调异常通知(0开 1关)")
    private String callbackLose;
    @ApiModelProperty(value = "查单返回是否传图(0开 1关)")
    private String isImage;
    @ApiModelProperty(value = "对账显示下发(0开 1关)")
    private String issue;
    @ApiModelProperty(value = "对账显示置顶(0开 1关)")
    private String pinned;

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

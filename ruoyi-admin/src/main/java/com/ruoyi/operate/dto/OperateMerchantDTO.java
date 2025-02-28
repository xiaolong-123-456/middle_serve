package com.ruoyi.operate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Data
public class OperateMerchantDTO implements Serializable
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户id")
    private Long id;
    @ApiModelProperty(value = "商户名称")
    private String mchName;
    @ApiModelProperty(value = "登录账号")
    private String loginAct;
    @ApiModelProperty(value = "登录密码")
    private String loginPwd;
    @ApiModelProperty(value = "支付密码")
    private String payPwd;
    @ApiModelProperty(value = "商户分类")
    private String mchType;
    @ApiModelProperty(value = "商户分类")
    private List<String> mchTypeList;
    @ApiModelProperty(value = "飞机群（群id）")
    private Long tgmGroup;
    @ApiModelProperty(value = "飞机群联系人")
    private String tgmContact;
    @ApiModelProperty(value = "代理商")
    private Long agentId;
    @ApiModelProperty(value = "代理商名称")
    private String agentName;
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "余额")
    private String balance;
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

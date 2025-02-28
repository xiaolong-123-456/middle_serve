package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 机器人设置对象 sys_robat
 * 
 * @author maser123
 * @date 2024-11-18
 */
public class SysRobat extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 用户id */
    @Excel(name = "用户id")
    private Long userId;

    /** 机器人管理群ID */
    @Excel(name = "机器人管理群ID")
    private Long tgmRobat;

    /** 四方技术群ID */
    @Excel(name = "四方技术群ID")
    private Long tgmTechnology;

    /** 异常报错群ID */
    @Excel(name = "异常报错群ID")
    private Long tgmError;


    /** 点数警告（元） */
    @Excel(name = "点数警告", readConverterExp = "元=")
    private String pointsWarn;

    /** 通道额度警告（元） */
    @Excel(name = "通道额度警告", readConverterExp = "元=")
    private String chaQuotaWarn;

    /** 警告附加讯息 */
    @Excel(name = "警告附加讯息")
    private String warnAppend;

    /** 拉单异常通知(0开 1关) */
    @Excel(name = "拉单异常通知(0开 1关)")
    private String orderLose;

    /** 商户回调异常通知(0开 1关) */
    @Excel(name = "商户回调异常通知(0开 1关)")
    private String callbackLose;

    /** 查单返回是否传图(0开 1关) */
    @Excel(name = "查单返回是否传图(0开 1关)")
    private String isImage;

    /** 对账显示下发(0开 1关) */
    @Excel(name = "对账显示下发(0开 1关)")
    private String issue;

    /** 对账显示置顶(0开 1关) */
    @Excel(name = "对账显示置顶(0开 1关)")
    private String pinned;

    /** 商户状态（0正常 1停用） */
    @Excel(name = "商户状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setTgmRobat(Long tgmRobat)
    {
        this.tgmRobat = tgmRobat;
    }

    public Long getTgmRobat()
    {
        return tgmRobat;
    }

    public Long getTgmError() {
        return tgmError;
    }

    public void setTgmError(Long tgmError) {
        this.tgmError = tgmError;
    }

    public void setTgmTechnology(Long tgmTechnology)
    {
        this.tgmTechnology = tgmTechnology;
    }

    public Long getTgmTechnology()
    {
        return tgmTechnology;
    }
    public void setPointsWarn(String pointsWarn) 
    {
        this.pointsWarn = pointsWarn;
    }

    public String getPointsWarn() 
    {
        return pointsWarn;
    }
    public void setChaQuotaWarn(String chaQuotaWarn) 
    {
        this.chaQuotaWarn = chaQuotaWarn;
    }

    public String getChaQuotaWarn() 
    {
        return chaQuotaWarn;
    }
    public void setWarnAppend(String warnAppend) 
    {
        this.warnAppend = warnAppend;
    }

    public String getWarnAppend() 
    {
        return warnAppend;
    }
    public void setOrderLose(String orderLose) 
    {
        this.orderLose = orderLose;
    }

    public String getOrderLose() 
    {
        return orderLose;
    }
    public void setCallbackLose(String callbackLose) 
    {
        this.callbackLose = callbackLose;
    }

    public String getCallbackLose() 
    {
        return callbackLose;
    }
    public void setIsImage(String isImage) 
    {
        this.isImage = isImage;
    }

    public String getIsImage() 
    {
        return isImage;
    }
    public void setIssue(String issue) 
    {
        this.issue = issue;
    }

    public String getIssue() 
    {
        return issue;
    }
    public void setPinned(String pinned) 
    {
        this.pinned = pinned;
    }

    public String getPinned() 
    {
        return pinned;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("tgmRobat", getTgmRobat())
            .append("tgmTechnology", getTgmTechnology())
            .append("tgmError", getTgmError())
            .append("pointsWarn", getPointsWarn())
            .append("chaQuotaWarn", getChaQuotaWarn())
            .append("warnAppend", getWarnAppend())
            .append("orderLose", getOrderLose())
            .append("callbackLose", getCallbackLose())
            .append("isImage", getIsImage())
            .append("issue", getIssue())
            .append("pinned", getPinned())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

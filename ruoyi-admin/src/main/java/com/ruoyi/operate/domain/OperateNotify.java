package com.ruoyi.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 回调记录对象 operate_notify
 * 
 * @author master123
 * @date 2024-12-25
 */
public class OperateNotify extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 订单id */
    private Long orderId;

    /** 商户id */
    @Excel(name = "商户id")
    private Long mchId;

    /** 商户名称 */
    @Excel(name = "商户名称")
    private String mchName;

    /** 商户订单号 */
    @Excel(name = "商户订单号")
    private String mchOrderNo;

    /** 平台订单号 */
    @Excel(name = "平台订单号")
    private String payOrderNo;

    /** 0支付 1退款 */
    @Excel(name = "0支付 1退款")
    private String orderType;

    /** 0成功 1失败 */
    @Excel(name = "0成功 1失败")
    private String notifyStatus;

    /** 回调次数 */
    @Excel(name = "回调次数")
    private Integer notifyTime;

    /** 回调时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date notifyDate;

    /** 通知地址 */
    private String notifyUrl;

    /** 商户返回消息 */
    private String mchMsg;

    /** 状态（0正常 1停用） */
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
    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }
    public void setMchId(Long mchId) 
    {
        this.mchId = mchId;
    }

    public Long getMchId() 
    {
        return mchId;
    }
    public void setMchName(String mchName) 
    {
        this.mchName = mchName;
    }

    public String getMchName() 
    {
        return mchName;
    }
    public void setMchOrderNo(String mchOrderNo) 
    {
        this.mchOrderNo = mchOrderNo;
    }

    public String getMchMsg() {
        return mchMsg;
    }

    public void setMchMsg(String mchMsg) {
        this.mchMsg = mchMsg;
    }

    public String getMchOrderNo()
    {
        return mchOrderNo;
    }
    public void setPayOrderNo(String payOrderNo) 
    {
        this.payOrderNo = payOrderNo;
    }

    public String getPayOrderNo() 
    {
        return payOrderNo;
    }
    public void setOrderType(String orderType) 
    {
        this.orderType = orderType;
    }

    public String getOrderType() 
    {
        return orderType;
    }
    public void setNotifyStatus(String notifyStatus) 
    {
        this.notifyStatus = notifyStatus;
    }

    public String getNotifyStatus() 
    {
        return notifyStatus;
    }
    public void setNotifyTime(Integer notifyTime) 
    {
        this.notifyTime = notifyTime;
    }

    public Integer getNotifyTime() 
    {
        return notifyTime;
    }
    public void setNotifyDate(Date notifyDate) 
    {
        this.notifyDate = notifyDate;
    }

    public Date getNotifyDate() 
    {
        return notifyDate;
    }
    public void setNotifyUrl(String notifyUrl) 
    {
        this.notifyUrl = notifyUrl;
    }

    public String getNotifyUrl() 
    {
        return notifyUrl;
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
            .append("orderId", getOrderId())
            .append("mchId", getMchId())
            .append("mchName", getMchName())
            .append("mchOrderNo", getMchOrderNo())
            .append("payOrderNo", getPayOrderNo())
            .append("orderType", getOrderType())
            .append("notifyStatus", getNotifyStatus())
            .append("notifyTime", getNotifyTime())
            .append("notifyDate", getNotifyDate())
            .append("notifyUrl", getNotifyUrl())
            .append("mchMsg", getMchMsg())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

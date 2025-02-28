package com.ruoyi.operate.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 补单记录对象 operate_replenishment
 * 
 * @author master123
 * @date 2024-12-25
 */
public class OperateReplenishment extends BaseEntity
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

    /** 通道标识ID */
    private Long chaLabId;

    /** 通道标识名称 */
    private String chaLabName;

    /** 支付通道ID */
    private Long payChannelId;

    /** 支付通道名称 */
    @Excel(name = "支付通道名称")
    private String payChannelName;

    /** 支付通道编码 */
    private String payChannelCode;

    /** 支付金额 */
    @Excel(name = "支付金额")
    private BigDecimal paymentAmount;

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
    public void setChaLabId(Long chaLabId) 
    {
        this.chaLabId = chaLabId;
    }

    public Long getChaLabId() 
    {
        return chaLabId;
    }
    public void setChaLabName(String chaLabName) 
    {
        this.chaLabName = chaLabName;
    }

    public String getChaLabName() 
    {
        return chaLabName;
    }
    public void setPayChannelId(Long payChannelId) 
    {
        this.payChannelId = payChannelId;
    }

    public Long getPayChannelId() 
    {
        return payChannelId;
    }
    public void setPayChannelName(String payChannelName) 
    {
        this.payChannelName = payChannelName;
    }

    public String getPayChannelName() 
    {
        return payChannelName;
    }
    public void setPayChannelCode(String payChannelCode) 
    {
        this.payChannelCode = payChannelCode;
    }

    public String getPayChannelCode() 
    {
        return payChannelCode;
    }
    public void setPaymentAmount(BigDecimal paymentAmount) 
    {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getPaymentAmount() 
    {
        return paymentAmount;
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
            .append("chaLabId", getChaLabId())
            .append("chaLabName", getChaLabName())
            .append("payChannelId", getPayChannelId())
            .append("payChannelName", getPayChannelName())
            .append("payChannelCode", getPayChannelCode())
            .append("paymentAmount", getPaymentAmount())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

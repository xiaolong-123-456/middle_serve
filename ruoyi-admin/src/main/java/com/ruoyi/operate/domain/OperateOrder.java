package com.ruoyi.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单数据对象 operate_order
 * 
 * @author master123
 * @date 2024-09-25
 */
@Data
public class OperateOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 商户ID */
    @Excel(name = "商户ID")
    private Long mchId;

    /** 商户名称 */
    @Excel(name = "商户名称")
    private String mchName;

    /** 代理商ID */
    private Long agentId;

    /** 代理商名称 */
    private String agentName;

    /** 产品ID */
    @Excel(name = "产品ID")
    private Long productId;

    /** 产品名称 */
    @Excel(name = "产品名称")
    private String productName;

    /** 通道标识ID */
    private Long chaLabId;

    /** 通道标识名称 */
    private String chaLabName;

    /** 支付通道ID */
    @Excel(name = "支付通道ID")
    private Long payChannelId;

    /** 支付通道名称 */
    @Excel(name = "支付通道名称")
    private String payChannelName;

    /** 支付通道编码 */
    @Excel(name = "支付通道名称")
    private String payChannelCode;

    /** 商户订单号 */
    @Excel(name = "商户订单号")
    private String mchOrderNo;

    /** 支付订单号(平台生成) */
    @Excel(name = "支付订单号(平台生成)")
    private String payOrderNo;

    /** 代理点位(费率) */
    private BigDecimal agentRate;

    /** 商户费率 */
    @Excel(name = "商户费率")
    private BigDecimal mchRate;

    /** 上游渠道费率 */
    @Excel(name = "上游渠道费率")
    private BigDecimal labelRate;

    /** 是否退款（0否 1是） */
    @Excel(name = "是否退款", readConverterExp = "0=否,1=是")
    private String isRefund;

    /** 退款金额 */
    @Excel(name = "退款金额")
    private BigDecimal refundAmount;

    /** 支付状态（0订单生成 1支付中 2支付成功 3支付失败 4处理完成 5已退款） */
    @Excel(name = "支付状态", readConverterExp = "0=订单生成,1=支付中,2=支付成功,3=支付失败,4=处理完成,5=已退款")
    private String paymentStatus;

    /** 支付金额 */
    @Excel(name = "支付金额")
    private BigDecimal paymentAmount;

    /** 商户入账(实际收到) */
    private BigDecimal mchActReceipt;

    /** 代理入账(实际收到) */
    private BigDecimal agentActReceipt;

    /** 平台入账(实际收到) */
    private BigDecimal platActReceipt;

    /** 上游渠道成本 */
    private BigDecimal payChaCost;

    /** 利润 */
    private BigDecimal profit;

    /** 渠道回调时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date chaCallbackDate;

    /** 耗时(秒) */
    private String elapsedTime;

    /** 商户回调url */
    private String notifyUrl;

    /** 商户前端跳转url */
    private String returnUrl;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
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
    public void setAgentId(Long agentId) 
    {
        this.agentId = agentId;
    }

    public Long getAgentId() 
    {
        return agentId;
    }
    public void setAgentName(String agentName) 
    {
        this.agentName = agentName;
    }

    public String getAgentName() 
    {
        return agentName;
    }
    public void setProductId(Long productId) 
    {
        this.productId = productId;
    }

    public Long getProductId() 
    {
        return productId;
    }
    public void setProductName(String productName) 
    {
        this.productName = productName;
    }

    public String getProductName() 
    {
        return productName;
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
    public void setAgentRate(BigDecimal agentRate) 
    {
        this.agentRate = agentRate;
    }

    public BigDecimal getAgentRate() 
    {
        return agentRate;
    }
    public void setMchRate(BigDecimal mchRate) 
    {
        this.mchRate = mchRate;
    }

    public BigDecimal getMchRate() 
    {
        return mchRate;
    }
    public void setLabelRate(BigDecimal labelRate) 
    {
        this.labelRate = labelRate;
    }

    public BigDecimal getLabelRate() 
    {
        return labelRate;
    }
    public void setIsRefund(String isRefund) 
    {
        this.isRefund = isRefund;
    }

    public String getIsRefund() 
    {
        return isRefund;
    }
    public void setRefundAmount(BigDecimal refundAmount) 
    {
        this.refundAmount = refundAmount;
    }

    public BigDecimal getRefundAmount() 
    {
        return refundAmount;
    }
    public void setPaymentStatus(String paymentStatus) 
    {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentStatus() 
    {
        return paymentStatus;
    }
    public void setPaymentAmount(BigDecimal paymentAmount) 
    {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getPaymentAmount() 
    {
        return paymentAmount;
    }
    public void setMchActReceipt(BigDecimal mchActReceipt) 
    {
        this.mchActReceipt = mchActReceipt;
    }

    public BigDecimal getMchActReceipt() 
    {
        return mchActReceipt;
    }
    public void setAgentActReceipt(BigDecimal agentActReceipt) 
    {
        this.agentActReceipt = agentActReceipt;
    }

    public BigDecimal getAgentActReceipt() 
    {
        return agentActReceipt;
    }
    public void setPlatActReceipt(BigDecimal platActReceipt) 
    {
        this.platActReceipt = platActReceipt;
    }

    public BigDecimal getPlatActReceipt() 
    {
        return platActReceipt;
    }
    public void setPayChaCost(BigDecimal payChaCost) 
    {
        this.payChaCost = payChaCost;
    }

    public BigDecimal getPayChaCost() 
    {
        return payChaCost;
    }
    public void setProfit(BigDecimal profit) 
    {
        this.profit = profit;
    }

    public BigDecimal getProfit() 
    {
        return profit;
    }
    public void setChaCallbackDate(Date chaCallbackDate) 
    {
        this.chaCallbackDate = chaCallbackDate;
    }

    public Date getChaCallbackDate() 
    {
        return chaCallbackDate;
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

    public String getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("mchId", getMchId())
            .append("mchName", getMchName())
            .append("agentId", getAgentId())
            .append("agentName", getAgentName())
            .append("productId", getProductId())
            .append("productName", getProductName())
            .append("chaLabId", getChaLabId())
            .append("chaLabName", getChaLabName())
            .append("payChannelId", getPayChannelId())
            .append("payChannelName", getPayChannelName())
            .append("mchOrderNo", getMchOrderNo())
            .append("payOrderNo", getPayOrderNo())
            .append("agentRate", getAgentRate())
            .append("mchRate", getMchRate())
            .append("labelRate", getLabelRate())
            .append("isRefund", getIsRefund())
            .append("refundAmount", getRefundAmount())
            .append("paymentStatus", getPaymentStatus())
            .append("paymentAmount", getPaymentAmount())
            .append("mchActReceipt", getMchActReceipt())
            .append("agentActReceipt", getAgentActReceipt())
            .append("platActReceipt", getPlatActReceipt())
            .append("payChaCost", getPayChaCost())
            .append("profit", getProfit())
            .append("chaCallbackDate", getChaCallbackDate())
            .append("elapsedTime", getElapsedTime())
            .append("notifyUrl", getNotifyUrl())
            .append("returnUrl", getReturnUrl())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

package com.ruoyi.operate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OperateOrderDTO extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    @ApiModelProperty(value = "id")
    private Long id;

    /** 商户ID */
    @ApiModelProperty(value = "商户ID")
    private Long mchId;

    /** 商户名称 */
    @ApiModelProperty(value = "商户名称")
    private String mchName;

    /** 代理商ID */
    @ApiModelProperty(value = "代理商ID")
    private Long agentId;

    /** 代理商名称 */
    @ApiModelProperty(value = "代理商名称")
    private String agentName;

    /** 产品ID */
    @ApiModelProperty(value = "产品ID")
    private Long productId;

    /** 产品名称 */
    @ApiModelProperty(value = "产品名称")
    private String productName;

    /** 通道标识ID */
    @ApiModelProperty(value = "通道标识ID")
    private Long chaLabId;

    /** 通道标识名称 */
    @ApiModelProperty(value = "通道标识名称")
    private String chaLabName;

    /** 支付通道ID */
    @ApiModelProperty(value = "支付通道ID")
    private Long payChannelId;

    /** 支付通道名称 */
    @ApiModelProperty(value = "支付通道名称")
    private String payChannelName;

    /** 支付通道编码 */
    @ApiModelProperty(value = "支付通道编码")
    private String payChannelCode;

    /** 商户订单号 */
    @ApiModelProperty(value = "商户订单号")
    private String mchOrderNo;

    /** 支付订单号(平台生成) */
    @ApiModelProperty(value = "支付订单号(平台生成)")
    private String payOrderNo;

    /** 代理点位(费率) */
    @ApiModelProperty(value = "代理点位(费率) ")
    private BigDecimal agentRate;

    /** 商户费率 */
    @ApiModelProperty(value = "商户费率")
    private BigDecimal mchRate;

    /** 上游渠道费率 */
    @ApiModelProperty(value = "上游渠道费率")
    private BigDecimal labelRate;

    /** 是否退款（0否 1是） */
    @ApiModelProperty(value = "是否退款（0否 1是）")
    private String isRefund;

    /** 退款金额 */
    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundAmount;

    /** 支付状态（0订单生成 1支付中 2支付成功 3支付失败 4处理完成 5已退款） */
    @ApiModelProperty(value = "支付状态（0订单生成 1支付中 2支付成功 3支付失败 4处理完成 5已退款）")
    private String paymentStatus;

    /** 支付金额 */
    @ApiModelProperty(value = "支付金额")
    private BigDecimal paymentAmount;

    /** 商户入账(实际收到) */
    @ApiModelProperty(value = "商户入账(实际收到)")
    private BigDecimal mchActReceipt;

    /** 代理入账(实际收到) */
    @ApiModelProperty(value = "代理入账(实际收到)")
    private BigDecimal agentActReceipt;

    /** 平台入账(实际收到) */
    @ApiModelProperty(value = "平台入账(实际收到)")
    private BigDecimal platActReceipt;

    /** 上游渠道成本 */
    @ApiModelProperty(value = "上游渠道成本")
    private BigDecimal payChaCost;

    /** 利润 */
    @ApiModelProperty(value = "利润")
    private BigDecimal profit;

    /** 最小金额 */
    @ApiModelProperty(value = "最小金额")
    private BigDecimal minAmount;

    /** 最大金额 */
    @ApiModelProperty(value = "最大金额")
    private BigDecimal maxAmount;

    /** 渠道回调时间 */
    @ApiModelProperty(value = "渠道回调时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date chaCallbackDate;

    /** 耗时 */
    @ApiModelProperty(value = "耗时")
    private String elapsedTime;

    @ApiModelProperty(value = "昨天")
    private String yesterDayTime;

    @ApiModelProperty(value = "开始时间")
    private String startDate;
    @ApiModelProperty(value = "结束时间")
    private String endDate;
    @ApiModelProperty(value = "昨天(查询订单统计数据)")
    private String yesterDayDate;
    @ApiModelProperty(value = "今天")
    private String toDayDate;
    @ApiModelProperty(value = "userId")
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
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

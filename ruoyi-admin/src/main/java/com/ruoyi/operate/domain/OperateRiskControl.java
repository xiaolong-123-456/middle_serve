package com.ruoyi.operate.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 风控设置对象 operate_risk_control
 * 
 * @author master123
 * @date 2024-09-09
 */
public class OperateRiskControl extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 风控id */
    private Long id;

    /** 当天交易总金额 */
    @Excel(name = "当天交易总金额")
    private BigDecimal totalAmount;

    /** 单笔最大金额 */
    @Excel(name = "单笔最大金额")
    private BigDecimal maxAmount;

    /** 单笔最小金额 */
    @Excel(name = "单笔最小金额")
    private BigDecimal minAmount;

    /** 金额类型(0连续任意金额 1固定金额) */
    @Excel(name = "金额类型(0连续任意金额 1固定金额)")
    private String amountType;

    /** 固定金额 */
    @Excel(name = "固定金额")
    private String fixedAmount;

    /** 交易开始时间 */
    @Excel(name = "交易开始时间")
    private String startTime;

    /** 交易结束时间 */
    @Excel(name = "交易结束时间")
    private String endTime;

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
    public void setTotalAmount(BigDecimal totalAmount) 
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() 
    {
        return totalAmount;
    }
    public void setMaxAmount(BigDecimal maxAmount) 
    {
        this.maxAmount = maxAmount;
    }

    public BigDecimal getMaxAmount() 
    {
        return maxAmount;
    }
    public void setMinAmount(BigDecimal minAmount) 
    {
        this.minAmount = minAmount;
    }

    public BigDecimal getMinAmount() 
    {
        return minAmount;
    }
    public void setAmountType(String amountType) 
    {
        this.amountType = amountType;
    }

    public String getAmountType() 
    {
        return amountType;
    }
    public void setFixedAmount(String fixedAmount) 
    {
        this.fixedAmount = fixedAmount;
    }

    public String getFixedAmount() 
    {
        return fixedAmount;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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
            .append("totalAmount", getTotalAmount())
            .append("maxAmount", getMaxAmount())
            .append("minAmount", getMinAmount())
            .append("amountType", getAmountType())
            .append("fixedAmount", getFixedAmount())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

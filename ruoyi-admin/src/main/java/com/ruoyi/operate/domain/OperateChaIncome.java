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
 * 渠道下发对象 operate_cha_income
 * 
 * @author master123
 * @date 2024-09-27
 */
@Data
public class OperateChaIncome extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 通道标识ID */
    @Excel(name = "通道标识ID")
    private Long chaLableId;

    /** 通道标识名称 */
    @Excel(name = "通道标识名称")
    private String chaLableName;

    /** 入款金额 */
    @Excel(name = "入款金额")
    private BigDecimal incomeAmount;

    /** 入款USDT */
    @Excel(name = "入款USDT")
    private BigDecimal USDT;

    /** USDT费率 */
    @Excel(name = "USDT费率")
    private BigDecimal usdtRate;

    /** 入款时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "入款时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date incomeDate;

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
    public String getChaLableName() {
        return chaLableName;
    }

    public void setChaLableName(String chaLableName) {
        this.chaLableName = chaLableName;
    }

    public void setIncomeAmount(BigDecimal incomeAmount)
    {
        this.incomeAmount = incomeAmount;
    }

    public BigDecimal getIncomeAmount() 
    {
        return incomeAmount;
    }
    public void setUSDT(BigDecimal USDT)
    {
        this.USDT = USDT;
    }

    public BigDecimal getUSDT()
    {
        return USDT;
    }
    public void setUsdtRate(BigDecimal usdtRate) 
    {
        this.usdtRate = usdtRate;
    }

    public BigDecimal getUsdtRate() 
    {
        return usdtRate;
    }
    public void setIncomeDate(Date incomeDate) 
    {
        this.incomeDate = incomeDate;
    }

    public Date getIncomeDate() 
    {
        return incomeDate;
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
            .append("chaLableId", getChaLableId())
            .append("chaLableName", getChaLableName())
            .append("incomeAmount", getIncomeAmount())
            .append("USDT", getUSDT())
            .append("usdtRate", getUsdtRate())
            .append("incomeDate", getIncomeDate())
            .append("remark", getRemark())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

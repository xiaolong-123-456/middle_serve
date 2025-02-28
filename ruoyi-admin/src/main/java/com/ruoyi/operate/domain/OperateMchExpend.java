package com.ruoyi.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商户下发对象 operate_mch_expend
 * 
 * @author master123
 * @date 2024-09-27
 */
public class OperateMchExpend extends BaseEntity
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

    /** 出款金额 */
    @Excel(name = "出款金额")
    private BigDecimal expendAmount;

    /** 出款USDT */
    @Excel(name = "出款USDT")
    private BigDecimal USDT;

    /** USDT费率 */
    @Excel(name = "USDT费率")
    private BigDecimal usdtRate;

    /** 出款时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出款时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expendDate;

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
    public void setExpendAmount(BigDecimal expendAmount) 
    {
        this.expendAmount = expendAmount;
    }

    public BigDecimal getExpendAmount() 
    {
        return expendAmount;
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
    public void setExpendDate(Date expendDate) 
    {
        this.expendDate = expendDate;
    }

    public Date getExpendDate() 
    {
        return expendDate;
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
            .append("mchId", getMchId())
            .append("mchName", getMchName())
            .append("expendAmount", getExpendAmount())
            .append("USDT", getUSDT())
            .append("usdtRate", getUsdtRate())
            .append("expendDate", getExpendDate())
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

package com.ruoyi.operate.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 支付通道信息对象 operate_pay_channel
 * 
 * @author master123
 * @date 2024-09-03
 */
@Data
public class OperatePayChannel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 支付通道id */
    private Long id;

    /** 通道编码 */
    private String chaCode;

    /** 通道名称 */
    @Excel(name = "通道名称")
    private String chaName;

    /** 通道费率 */
    @Excel(name = "通道费率")
    private BigDecimal chaRate;


    /** 通道标识id */
    private Long chaLabId;

    /** 风控id */
    private Long riskControlId;

    /** 通道类型（0正常 1测试 2防拉空） */
    private String chaType;


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
    public void setChaCode(String chaCode) 
    {
        this.chaCode = chaCode;
    }

    public String getChaCode() 
    {
        return chaCode;
    }
    public void setChaName(String chaName) 
    {
        this.chaName = chaName;
    }

    public String getChaName() 
    {
        return chaName;
    }
    public void setChaRate(BigDecimal chaRate) 
    {
        this.chaRate = chaRate;
    }

    public BigDecimal getChaRate() 
    {
        return chaRate;
    }
    public void setChaLabId(Long chaLabId) 
    {
        this.chaLabId = chaLabId;
    }

    public Long getChaLabId() 
    {
        return chaLabId;
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
            .append("chaCode", getChaCode())
            .append("chaName", getChaName())
            .append("chaRate", getChaRate())
            .append("chaLabId", getChaLabId())
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

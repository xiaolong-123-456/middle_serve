package com.ruoyi.operate.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 商户和产品单独配置对象 operate_mch_pro_alone
 * 
 * @author master123
 * @date 2024-09-13
 */
public class OperateMchProAlone extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 商户和产品配置详情id */
    @Excel(name = "商户和产品配置详情id")
    private Long mchProId;

    /** 支付通道id */
    @Excel(name = "支付通道id")
    private Long payChannelId;

    /** 权重(数值 数值越小权重越高) */
    @Excel(name = "权重(数值 数值越小权重越高)")
    private Integer sort;

    /** 风控id */
    @Excel(name = "风控id")
    private Long riskControlId;

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
    public void setMchProId(Long mchProId) 
    {
        this.mchProId = mchProId;
    }

    public Long getMchProId() 
    {
        return mchProId;
    }
    public void setPayChannelId(Long payChannelId) 
    {
        this.payChannelId = payChannelId;
    }

    public Long getPayChannelId() 
    {
        return payChannelId;
    }
    public void setSort(Integer sort) 
    {
        this.sort = sort;
    }

    public Integer getSort() 
    {
        return sort;
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

    public Long getRiskControlId() {
        return riskControlId;
    }

    public void setRiskControlId(Long riskControlId) {
        this.riskControlId = riskControlId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("mchProId", getMchProId())
            .append("payChannelId", getPayChannelId())
            .append("sort", getSort())
            .append("riskControlId", getRiskControlId())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

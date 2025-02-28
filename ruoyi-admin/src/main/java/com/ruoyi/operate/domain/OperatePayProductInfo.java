package com.ruoyi.operate.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 支付产品详情信息对象 operate_pay_product_info
 * 
 * @author master123
 * @date 2024-09-09
 */
public class OperatePayProductInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 支付产品详情id */
    private Long id;

    /** 支付产品id */
    @Excel(name = "支付产品id")
    private Long payProductId;

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
    public void setPayProductId(Long payProductId) 
    {
        this.payProductId = payProductId;
    }

    public Long getPayProductId() 
    {
        return payProductId;
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
    public void setRiskControlId(Long riskControlId) 
    {
        this.riskControlId = riskControlId;
    }

    public Long getRiskControlId() 
    {
        return riskControlId;
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
            .append("payProductId", getPayProductId())
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

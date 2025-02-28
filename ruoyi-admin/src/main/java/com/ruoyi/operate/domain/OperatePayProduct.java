package com.ruoyi.operate.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 支付产品信息对象 operate_pay_product
 * 
 * @author master123
 * @date 2024-09-05
 */
@Data
public class OperatePayProduct extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 支付产品id */
    private Long id;

    /** 支付产品名称 */
    @Excel(name = "支付产品名称")
    private String proName;

    /** 商户费率 */
    @Excel(name = "商户费率")
    private BigDecimal mchRate;

    /** 接口类型(0单独 1轮询) */
    @Excel(name = "接口类型(0单独 1轮询)")
    private String portType;


    /** 接口类型(0全部  代表上游) */
    private Long chaLabId;

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
    public void setProName(String proName) 
    {
        this.proName = proName;
    }

    public String getProName() 
    {
        return proName;
    }
    public void setMchRate(BigDecimal mchRate) 
    {
        this.mchRate = mchRate;
    }

    public BigDecimal getMchRate() 
    {
        return mchRate;
    }
    public void setPortType(String portType) 
    {
        this.portType = portType;
    }

    public String getPortType() 
    {
        return portType;
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
            .append("proName", getProName())
            .append("mchRate", getMchRate())
            .append("portType", getPortType())
            .append("chaLabId", getChaLabId())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

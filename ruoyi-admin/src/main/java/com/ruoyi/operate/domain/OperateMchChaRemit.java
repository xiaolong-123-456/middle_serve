package com.ruoyi.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商户和渠道打款对象 operate_mch_cha_remit
 * 
 * @author master123
 * @date 2024-09-30
 */
public class OperateMchChaRemit extends BaseEntity
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

    /** 通道标识ID */
    @Excel(name = "通道标识ID")
    private Long chaLableId;

    /** 通道标识名称 */
    @Excel(name = "通道标识名称")
    private String chaLableName;

    /** 打款金额 */
    @Excel(name = "打款金额")
    private BigDecimal remitAmount;

    /** 打款时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "打款时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date remitDate;

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
    public void setChaLableId(Long chaLableId) 
    {
        this.chaLableId = chaLableId;
    }

    public Long getChaLableId() 
    {
        return chaLableId;
    }
    public void setChaLableName(String chaLableName) 
    {
        this.chaLableName = chaLableName;
    }

    public String getChaLableName() 
    {
        return chaLableName;
    }
    public void setRemitAmount(BigDecimal remitAmount) 
    {
        this.remitAmount = remitAmount;
    }

    public BigDecimal getRemitAmount() 
    {
        return remitAmount;
    }
    public void setRemitDate(Date remitDate) 
    {
        this.remitDate = remitDate;
    }

    public Date getRemitDate() 
    {
        return remitDate;
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
            .append("chaLableId", getChaLableId())
            .append("chaLableName", getChaLableName())
            .append("remitAmount", getRemitAmount())
            .append("remitDate", getRemitDate())
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

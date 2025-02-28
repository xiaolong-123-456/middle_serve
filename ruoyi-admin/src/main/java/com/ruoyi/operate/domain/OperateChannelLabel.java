package com.ruoyi.operate.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 通道标识信息对象 operate_channel_label
 * 
 * @author master123
 * @date 2024-09-03
 */
@Data
public class OperateChannelLabel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 通道标识id */
    private Long id;

    /** 通道标识代码 */
    @Excel(name = "通道标识代码")
    private String chaLabCode;

    /** 通道标识名称 */
    @Excel(name = "通道标识名称")
    private String chaLabName;

    /** 飞机群（群id） */
    private Long tgmGroup;

    /** 飞机群联系人 */
    private String tgmContact;

    /** 通知ip */
    @Excel(name = "通知ip")
    private String informIp;

    /** 配置描述 */
    private String configDescribe;

    /** 商户id */
    private String mchId;

    /** 商户密匙 */
    private String mchKey;

    /** 支付类型(h5，native,guide) */
    private String payType;

    /** 支付方式(wechat,alipay,guide) */
    private String payWay;

    /** 网关 */
    private String gateway;

    /** 下单接口 */
    private String buyPort;

    /** 查询接口 */
    private String queryPort;

    /** 下单参数 */
    private String buyParams;

    /** 查询参数  */
    private String checkParams;

    /** 下单返回参数 */
    private String returnParams;

    /** 回调参数 */
    private String notifyParams;

    /** 查询返回参数 */
    private String queryRenParams;

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
    public void setChaLabCode(String chaLabCode) 
    {
        this.chaLabCode = chaLabCode;
    }

    public String getChaLabCode() 
    {
        return chaLabCode;
    }
    public void setChaLabName(String chaLabName) 
    {
        this.chaLabName = chaLabName;
    }

    public String getChaLabName() 
    {
        return chaLabName;
    }
    public void setTgmGroup(Long tgmGroup) 
    {
        this.tgmGroup = tgmGroup;
    }

    public Long getTgmGroup() 
    {
        return tgmGroup;
    }
    public void setTgmContact(String tgmContact) 
    {
        this.tgmContact = tgmContact;
    }

    public String getTgmContact() 
    {
        return tgmContact;
    }
    public void setInformIp(String informIp) 
    {
        this.informIp = informIp;
    }

    public String getInformIp() 
    {
        return informIp;
    }
    public void setConfigDescribe(String configDescribe) 
    {
        this.configDescribe = configDescribe;
    }

    public String getConfigDescribe() 
    {
        return configDescribe;
    }
    public void setMchId(String mchId)
    {
        this.mchId = mchId;
    }

    public String getMchId()
    {
        return mchId;
    }
    public void setMchKey(String mchKey) 
    {
        this.mchKey = mchKey;
    }

    public String getMchKey() 
    {
        return mchKey;
    }
    public void setPayType(String payType) 
    {
        this.payType = payType;
    }

    public String getPayType() 
    {
        return payType;
    }
    public void setPayWay(String payWay) 
    {
        this.payWay = payWay;
    }

    public String getPayWay() 
    {
        return payWay;
    }
    public void setGateway(String gateway) 
    {
        this.gateway = gateway;
    }

    public String getGateway() 
    {
        return gateway;
    }
    public void setBuyPort(String buyPort) 
    {
        this.buyPort = buyPort;
    }

    public String getBuyPort() 
    {
        return buyPort;
    }
    public void setQueryPort(String queryPort) 
    {
        this.queryPort = queryPort;
    }

    public String getQueryPort() 
    {
        return queryPort;
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
            .append("chaLabCode", getChaLabCode())
            .append("chaLabName", getChaLabName())
            .append("remark", getRemark())
            .append("tgmGroup", getTgmGroup())
            .append("tgmContact", getTgmContact())
            .append("informIp", getInformIp())
            .append("configDescribe", getConfigDescribe())
            .append("mchId", getMchId())
            .append("mchKey", getMchKey())
            .append("payType", getPayType())
            .append("payWay", getPayWay())
            .append("gateway", getGateway())
            .append("buyPort", getBuyPort())
            .append("queryPort", getQueryPort())
            .append("buyParams", getBuyParams())
            .append("returnParams", getReturnParams())
            .append("notifyParams", getReturnParams())
            .append("checkParams", getCheckParams())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

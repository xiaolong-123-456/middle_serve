package com.ruoyi.operate.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 商户信息对象 operate_merchant
 * 
 * @author zjj
 * @date 2024-08-19
 */
@Data
public class OperateMerchant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 商户id */
    private Long id;

    /** 商户名称 */
    @Excel(name = "商户名称")
    private String mchName;

    /** 登录账号 */
    private String loginAct;

    /** 登录密码 */
    private String loginPwd;

    /** 支付密码 */
    private String payPwd;

    /** 商户分类 */
    @Excel(name = "商户分类")
    private String mchType;

    /** 飞机群（群id） */
    private Long tgmGroup;

    /** 飞机群联系人 */
    private String tgmContact;

    /** 代理商 */
    private Long agentId;

    /** 手机号 */
    private String mobile;

    /** mchKey */
    private String mchKey;

    /** 余额 */
    private BigDecimal balance;

    /** 用户id */
    private Long userId;

    /** 商户状态（0正常 1停用） */
    @Excel(name = "商户状态", readConverterExp = "0=正常,1=停用")
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
    public void setMchName(String mchName) 
    {
        this.mchName = mchName;
    }

    public String getMchName() 
    {
        return mchName;
    }
    public void setLoginAct(String loginAct) 
    {
        this.loginAct = loginAct;
    }

    public String getLoginAct() 
    {
        return loginAct;
    }
    public void setLoginPwd(String loginPwd) 
    {
        this.loginPwd = loginPwd;
    }

    public String getLoginPwd() 
    {
        return loginPwd;
    }
    public void setPayPwd(String payPwd) 
    {
        this.payPwd = payPwd;
    }

    public String getPayPwd() 
    {
        return payPwd;
    }
    public void setMchType(String mchType)
    {
        this.mchType = mchType;
    }

    public String getMchType()
    {
        return mchType;
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
    public void setAgentId(Long agentId) 
    {
        this.agentId = agentId;
    }

    public Long getAgentId() 
    {
        return agentId;
    }
    public void setMobile(String mobile) 
    {
        this.mobile = mobile;
    }

    public String getMobile() 
    {
        return mobile;
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
            .append("mchName", getMchName())
            .append("loginAct", getLoginAct())
            .append("loginPwd", getLoginPwd())
            .append("payPwd", getPayPwd())
            .append("mchType", getMchType())
            .append("tgmGroup", getTgmGroup())
            .append("tgmContact", getTgmContact())
            .append("agentId", getAgentId())
            .append("mobile", getMobile())
            .append("mchKey", getMchKey())
            .append("balance", getBalance())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

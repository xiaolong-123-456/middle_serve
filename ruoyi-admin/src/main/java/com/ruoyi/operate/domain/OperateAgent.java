package com.ruoyi.operate.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 所有代理商对象 operate_agent
 * 
 * @author master123
 * @date 2024-08-26
 */
@Data
public class OperateAgent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 代理商id */
    private Long id;

    /** 代理商名称 */
    @Excel(name = "代理商名称")
    private String agentName;

    /** 登录账号 */
    private String loginAct;

    /** 登录密码 */
    private String loginPwd;

    /** 支付密码 */
    private String payPwd;

    /** 密钥 */
    private String agentKey;

    /** 手机号 */
    private String mobile;

    /** 余额 */
    private BigDecimal balance;

    /** 用户id */
    private Long userId;

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
    public void setAgentName(String agentName) 
    {
        this.agentName = agentName;
    }

    public String getAgentName() 
    {
        return agentName;
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
            .append("agentName", getAgentName())
            .append("loginAct", getLoginAct())
            .append("loginPwd", getLoginPwd())
            .append("payPwd", getPayPwd())
            .append("mobile", getMobile())
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

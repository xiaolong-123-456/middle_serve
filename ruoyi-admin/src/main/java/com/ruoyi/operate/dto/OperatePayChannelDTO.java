package com.ruoyi.operate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

@Data
public class OperatePayChannelDTO extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "通道编码")
    private String chaCode;
    @ApiModelProperty(value = "通道名称")
    private String chaName;
    @ApiModelProperty(value = "接口代码")
    private String portCode;
    @ApiModelProperty(value = "通道费率")
    private String chaRate;
    @ApiModelProperty(value = "通道标识id")
    private Long chaLabId;
    @ApiModelProperty(value = "通道标识名称")
    private String chaLabName;
    @ApiModelProperty(value = "风控id")
    private Long riskControlId;
    @ApiModelProperty(value = "状态（0正常 1停用）")
    private String status;
    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    private String delFlag;
    @ApiModelProperty(value = "创建者")
    private String createBy;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @ApiModelProperty(value = "更新者")
    private String updateBy;
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

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

package com.ruoyi.operate.service;

import com.ruoyi.operate.domain.OperateRiskControl;

import java.util.List;

/**
 * 风控设置Service接口
 * 
 * @author master123
 * @date 2024-09-09
 */
public interface IOperateRiskControlService 
{
    /**
     * 查询风控设置
     * 
     * @param id 风控设置主键
     * @return 风控设置
     */
    public OperateRiskControl selectOperateRiskControlById(Long id);

    /**
     * 查询风控设置列表
     * 
     * @param operateRiskControl 风控设置
     * @return 风控设置集合
     */
    public List<OperateRiskControl> selectOperateRiskControlList(OperateRiskControl operateRiskControl);

    /**
     * 新增风控设置
     * 
     * @param operateRiskControl 风控设置
     * @return 结果
     */
    public int insertOperateRiskControl(OperateRiskControl operateRiskControl);

    /**
     * 修改风控设置
     * 
     * @param operateRiskControl 风控设置
     * @return 结果
     */
    public int updateOperateRiskControl(OperateRiskControl operateRiskControl);

    /**
     * 批量删除风控设置
     * 
     * @param ids 需要删除的风控设置主键集合
     * @return 结果
     */
    public int deleteOperateRiskControlByIds(Long[] ids);

    /**
     * 删除风控设置信息
     * 
     * @param id 风控设置主键
     * @return 结果
     */
    public int deleteOperateRiskControlById(Long id);
}

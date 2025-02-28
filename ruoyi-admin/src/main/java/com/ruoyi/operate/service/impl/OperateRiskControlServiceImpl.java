package com.ruoyi.operate.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.operate.domain.OperateRiskControl;
import com.ruoyi.operate.mapper.OperateRiskControlMapper;
import com.ruoyi.operate.service.IOperateRiskControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 风控设置Service业务层处理
 * 
 * @author master123
 * @date 2024-09-09
 */
@Service
public class OperateRiskControlServiceImpl implements IOperateRiskControlService 
{
    @Autowired
    private OperateRiskControlMapper operateRiskControlMapper;

    /**
     * 查询风控设置
     * 
     * @param id 风控设置主键
     * @return 风控设置
     */
    @Override
    public OperateRiskControl selectOperateRiskControlById(Long id)
    {
        return operateRiskControlMapper.selectOperateRiskControlById(id);
    }

    /**
     * 查询风控设置列表
     * 
     * @param operateRiskControl 风控设置
     * @return 风控设置
     */
    @Override
    public List<OperateRiskControl> selectOperateRiskControlList(OperateRiskControl operateRiskControl)
    {
        return operateRiskControlMapper.selectOperateRiskControlList(operateRiskControl);
    }

    /**
     * 新增风控设置
     * 
     * @param operateRiskControl 风控设置
     * @return 结果
     */
    @Override
    public int insertOperateRiskControl(OperateRiskControl operateRiskControl)
    {
        operateRiskControl.setCreateTime(DateUtils.getNowDate());
        return operateRiskControlMapper.insertOperateRiskControl(operateRiskControl);
    }

    /**
     * 修改风控设置
     * 
     * @param operateRiskControl 风控设置
     * @return 结果
     */
    @Override
    public int updateOperateRiskControl(OperateRiskControl operateRiskControl)
    {
        operateRiskControl.setUpdateTime(DateUtils.getNowDate());
        return operateRiskControlMapper.updateOperateRiskControl(operateRiskControl);
    }

    /**
     * 批量删除风控设置
     * 
     * @param ids 需要删除的风控设置主键
     * @return 结果
     */
    @Override
    public int deleteOperateRiskControlByIds(Long[] ids)
    {
        return operateRiskControlMapper.deleteOperateRiskControlByIds(ids);
    }

    /**
     * 删除风控设置信息
     * 
     * @param id 风控设置主键
     * @return 结果
     */
    @Override
    public int deleteOperateRiskControlById(Long id)
    {
        return operateRiskControlMapper.deleteOperateRiskControlById(id);
    }
}

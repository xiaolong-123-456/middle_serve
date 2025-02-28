package com.ruoyi.operate.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.operate.domain.OperateAgent;
import com.ruoyi.operate.mapper.OperateAgentMapper;
import com.ruoyi.operate.service.IOperateAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 所有代理商Service业务层处理
 * 
 * @author master123
 * @date 2024-08-26
 */
@Service
public class OperateAgentServiceImpl implements IOperateAgentService 
{
    @Autowired
    private OperateAgentMapper operateAgentMapper;

    /**
     * 查询所有代理商
     * 
     * @param id 所有代理商主键
     * @return 所有代理商
     */
    @Override
    public OperateAgent selectOperateAgentById(Long id)
    {
        return operateAgentMapper.selectOperateAgentById(id);
    }

    /**
     * 查询所有代理商列表
     * 
     * @param operateAgent 所有代理商
     * @return 所有代理商
     */
    @Override
    public List<OperateAgent> selectOperateAgentList(OperateAgent operateAgent)
    {
        return operateAgentMapper.selectOperateAgentList(operateAgent);
    }

    /**
     * 新增所有代理商
     * 
     * @param operateAgent 所有代理商
     * @return 结果
     */
    @Override
    public int insertOperateAgent(OperateAgent operateAgent)
    {
        operateAgent.setCreateTime(DateUtils.getNowDate());
        return operateAgentMapper.insertOperateAgent(operateAgent);
    }

    /**
     * 修改所有代理商
     * 
     * @param operateAgent 所有代理商
     * @return 结果
     */
    @Override
    public int updateOperateAgent(OperateAgent operateAgent)
    {
        operateAgent.setUpdateTime(DateUtils.getNowDate());
        return operateAgentMapper.updateOperateAgent(operateAgent);
    }

    /**
     * 批量删除所有代理商
     * 
     * @param ids 需要删除的所有代理商主键
     * @return 结果
     */
    @Override
    public int deleteOperateAgentByIds(Long[] ids)
    {
        return operateAgentMapper.deleteOperateAgentByIds(ids);
    }

    /**
     * 删除所有代理商信息
     * 
     * @param id 所有代理商主键
     * @return 结果
     */
    @Override
    public int deleteOperateAgentById(Long id)
    {
        return operateAgentMapper.deleteOperateAgentById(id);
    }

    @Override
    public List<OperateAgent> queryOperateAgentAll() {
        return operateAgentMapper.selectOperateAgentAll();
    }

    @Override
    public OperateAgent queryOperateAgentByUserId(Long userId) {
        return operateAgentMapper.selectOperateAgentByUserId(userId);
    }
}

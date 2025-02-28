package com.ruoyi.operate.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.operate.domain.OperateChaIncome;
import com.ruoyi.operate.mapper.OperateChaIncomeMapper;
import com.ruoyi.operate.service.IOperateChaIncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 渠道下发Service业务层处理
 * 
 * @author master123
 * @date 2024-09-27
 */
@Service
public class OperateChaIncomeServiceImpl implements IOperateChaIncomeService 
{
    @Autowired
    private OperateChaIncomeMapper operateChaIncomeMapper;

    /**
     * 查询渠道下发
     * 
     * @param id 渠道下发主键
     * @return 渠道下发
     */
    @Override
    public OperateChaIncome selectOperateChaIncomeById(Long id)
    {
        return operateChaIncomeMapper.selectOperateChaIncomeById(id);
    }

    /**
     * 查询渠道下发列表
     * 
     * @param operateChaIncome 渠道下发
     * @return 渠道下发
     */
    @Override
    public List<OperateChaIncome> selectOperateChaIncomeList(OperateChaIncome operateChaIncome)
    {
        return operateChaIncomeMapper.selectOperateChaIncomeList(operateChaIncome);
    }

    /**
     * 新增渠道下发
     * 
     * @param operateChaIncome 渠道下发
     * @return 结果
     */
    @Override
    public int insertOperateChaIncome(OperateChaIncome operateChaIncome)
    {
        operateChaIncome.setCreateTime(DateUtils.getNowDate());
        return operateChaIncomeMapper.insertOperateChaIncome(operateChaIncome);
    }

    /**
     * 修改渠道下发
     * 
     * @param operateChaIncome 渠道下发
     * @return 结果
     */
    @Override
    public int updateOperateChaIncome(OperateChaIncome operateChaIncome)
    {
        operateChaIncome.setUpdateTime(DateUtils.getNowDate());
        return operateChaIncomeMapper.updateOperateChaIncome(operateChaIncome);
    }

    /**
     * 批量删除渠道下发
     * 
     * @param ids 需要删除的渠道下发主键
     * @return 结果
     */
    @Override
    public int deleteOperateChaIncomeByIds(Long[] ids)
    {
        return operateChaIncomeMapper.deleteOperateChaIncomeByIds(ids);
    }

    /**
     * 删除渠道下发信息
     * 
     * @param id 渠道下发主键
     * @return 结果
     */
    @Override
    public int deleteOperateChaIncomeById(Long id)
    {
        return operateChaIncomeMapper.deleteOperateChaIncomeById(id);
    }
}

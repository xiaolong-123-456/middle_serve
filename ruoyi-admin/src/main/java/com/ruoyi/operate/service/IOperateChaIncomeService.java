package com.ruoyi.operate.service;

import com.ruoyi.operate.domain.OperateChaIncome;

import java.util.List;

/**
 * 渠道下发Service接口
 * 
 * @author master123
 * @date 2024-09-27
 */
public interface IOperateChaIncomeService 
{
    /**
     * 查询渠道下发
     * 
     * @param id 渠道下发主键
     * @return 渠道下发
     */
    public OperateChaIncome selectOperateChaIncomeById(Long id);

    /**
     * 查询渠道下发列表
     * 
     * @param operateChaIncome 渠道下发
     * @return 渠道下发集合
     */
    public List<OperateChaIncome> selectOperateChaIncomeList(OperateChaIncome operateChaIncome);

    /**
     * 新增渠道下发
     * 
     * @param operateChaIncome 渠道下发
     * @return 结果
     */
    public int insertOperateChaIncome(OperateChaIncome operateChaIncome);

    /**
     * 修改渠道下发
     * 
     * @param operateChaIncome 渠道下发
     * @return 结果
     */
    public int updateOperateChaIncome(OperateChaIncome operateChaIncome);

    /**
     * 批量删除渠道下发
     * 
     * @param ids 需要删除的渠道下发主键集合
     * @return 结果
     */
    public int deleteOperateChaIncomeByIds(Long[] ids);

    /**
     * 删除渠道下发信息
     * 
     * @param id 渠道下发主键
     * @return 结果
     */
    public int deleteOperateChaIncomeById(Long id);
}

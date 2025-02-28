package com.ruoyi.operate.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.operate.domain.OperateMchExpend;
import com.ruoyi.operate.mapper.OperateMchExpendMapper;
import com.ruoyi.operate.service.IOperateMchExpendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商户下发Service业务层处理
 * 
 * @author master123
 * @date 2024-09-27
 */
@Service
public class OperateMchExpendServiceImpl implements IOperateMchExpendService 
{
    @Autowired
    private OperateMchExpendMapper operateMchExpendMapper;

    /**
     * 查询商户下发
     * 
     * @param id 商户下发主键
     * @return 商户下发
     */
    @Override
    public OperateMchExpend selectOperateMchExpendById(Long id)
    {
        return operateMchExpendMapper.selectOperateMchExpendById(id);
    }

    /**
     * 查询商户下发列表
     * 
     * @param operateMchExpend 商户下发
     * @return 商户下发
     */
    @Override
    public List<OperateMchExpend> selectOperateMchExpendList(OperateMchExpend operateMchExpend)
    {
        return operateMchExpendMapper.selectOperateMchExpendList(operateMchExpend);
    }

    /**
     * 新增商户下发
     * 
     * @param operateMchExpend 商户下发
     * @return 结果
     */
    @Override
    public int insertOperateMchExpend(OperateMchExpend operateMchExpend)
    {
        operateMchExpend.setCreateTime(DateUtils.getNowDate());
        return operateMchExpendMapper.insertOperateMchExpend(operateMchExpend);
    }

    /**
     * 修改商户下发
     * 
     * @param operateMchExpend 商户下发
     * @return 结果
     */
    @Override
    public int updateOperateMchExpend(OperateMchExpend operateMchExpend)
    {
        operateMchExpend.setUpdateTime(DateUtils.getNowDate());
        return operateMchExpendMapper.updateOperateMchExpend(operateMchExpend);
    }

    /**
     * 批量删除商户下发
     * 
     * @param ids 需要删除的商户下发主键
     * @return 结果
     */
    @Override
    public int deleteOperateMchExpendByIds(Long[] ids)
    {
        return operateMchExpendMapper.deleteOperateMchExpendByIds(ids);
    }

    /**
     * 删除商户下发信息
     * 
     * @param id 商户下发主键
     * @return 结果
     */
    @Override
    public int deleteOperateMchExpendById(Long id)
    {
        return operateMchExpendMapper.deleteOperateMchExpendById(id);
    }
}

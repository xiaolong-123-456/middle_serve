package com.ruoyi.operate.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.operate.domain.OperateReplenishment;
import com.ruoyi.operate.mapper.OperateReplenishmentMapper;
import com.ruoyi.operate.service.IOperateReplenishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 补单记录Service业务层处理
 * 
 * @author master123
 * @date 2024-12-25
 */
@Service
public class OperateReplenishmentServiceImpl implements IOperateReplenishmentService 
{
    @Autowired
    private OperateReplenishmentMapper operateReplenishmentMapper;

    /**
     * 查询补单记录
     * 
     * @param id 补单记录主键
     * @return 补单记录
     */
    @Override
    public OperateReplenishment selectOperateReplenishmentById(Long id)
    {
        return operateReplenishmentMapper.selectOperateReplenishmentById(id);
    }

    /**
     * 查询补单记录列表
     * 
     * @param operateReplenishment 补单记录
     * @return 补单记录
     */
    @Override
    public List<OperateReplenishment> selectOperateReplenishmentList(OperateReplenishment operateReplenishment)
    {
        return operateReplenishmentMapper.selectOperateReplenishmentList(operateReplenishment);
    }

    /**
     * 新增补单记录
     * 
     * @param operateReplenishment 补单记录
     * @return 结果
     */
    @Override
    public int insertOperateReplenishment(OperateReplenishment operateReplenishment)
    {
        operateReplenishment.setCreateTime(DateUtils.getNowDate());
        return operateReplenishmentMapper.insertOperateReplenishment(operateReplenishment);
    }

    /**
     * 修改补单记录
     * 
     * @param operateReplenishment 补单记录
     * @return 结果
     */
    @Override
    public int updateOperateReplenishment(OperateReplenishment operateReplenishment)
    {
        operateReplenishment.setUpdateTime(DateUtils.getNowDate());
        return operateReplenishmentMapper.updateOperateReplenishment(operateReplenishment);
    }

    /**
     * 批量删除补单记录
     * 
     * @param ids 需要删除的补单记录主键
     * @return 结果
     */
    @Override
    public int deleteOperateReplenishmentByIds(Long[] ids)
    {
        return operateReplenishmentMapper.deleteOperateReplenishmentByIds(ids);
    }

    /**
     * 删除补单记录信息
     * 
     * @param id 补单记录主键
     * @return 结果
     */
    @Override
    public int deleteOperateReplenishmentById(Long id)
    {
        return operateReplenishmentMapper.deleteOperateReplenishmentById(id);
    }
}

package com.ruoyi.operate.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.operate.domain.OperateNotify;
import com.ruoyi.operate.mapper.OperateNotifyMapper;
import com.ruoyi.operate.service.IOperateNotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 回调记录Service业务层处理
 * 
 * @author master123
 * @date 2024-12-25
 */
@Service
public class OperateNotifyServiceImpl implements IOperateNotifyService 
{
    @Autowired
    private OperateNotifyMapper operateNotifyMapper;

    /**
     * 查询回调记录
     * 
     * @param id 回调记录主键
     * @return 回调记录
     */
    @Override
    public OperateNotify selectOperateNotifyById(Long id)
    {
        return operateNotifyMapper.selectOperateNotifyById(id);
    }

    @Override
    public OperateNotify selectOperateNotifyByOrderId(Long orderId) {
        return operateNotifyMapper.selectOperateNotifyByOrderId(orderId);
    }

    /**
     * 查询回调记录列表
     * 
     * @param operateNotify 回调记录
     * @return 回调记录
     */
    @Override
    public List<OperateNotify> selectOperateNotifyList(OperateNotify operateNotify)
    {
        return operateNotifyMapper.selectOperateNotifyList(operateNotify);
    }

    /**
     * 新增回调记录
     * 
     * @param operateNotify 回调记录
     * @return 结果
     */
    @Override
    public int insertOperateNotify(OperateNotify operateNotify)
    {
        operateNotify.setCreateTime(DateUtils.getNowDate());
        return operateNotifyMapper.insertOperateNotify(operateNotify);
    }

    /**
     * 修改回调记录
     * 
     * @param operateNotify 回调记录
     * @return 结果
     */
    @Override
    public int updateOperateNotify(OperateNotify operateNotify)
    {
        operateNotify.setUpdateTime(DateUtils.getNowDate());
        return operateNotifyMapper.updateOperateNotify(operateNotify);
    }

    /**
     * 批量删除回调记录
     * 
     * @param ids 需要删除的回调记录主键
     * @return 结果
     */
    @Override
    public int deleteOperateNotifyByIds(Long[] ids)
    {
        return operateNotifyMapper.deleteOperateNotifyByIds(ids);
    }

    /**
     * 删除回调记录信息
     * 
     * @param id 回调记录主键
     * @return 结果
     */
    @Override
    public int deleteOperateNotifyById(Long id)
    {
        return operateNotifyMapper.deleteOperateNotifyById(id);
    }
}

package com.ruoyi.operate.service;

import com.ruoyi.operate.domain.OperateNotify;

import java.util.List;

/**
 * 回调记录Service接口
 * 
 * @author master123
 * @date 2024-12-25
 */
public interface IOperateNotifyService 
{
    /**
     * 查询回调记录
     * 
     * @param id 回调记录主键
     * @return 回调记录
     */
    public OperateNotify selectOperateNotifyById(Long id);

    /**
     * 查询回调记录
     *
     * @param orderId 订单id
     * @return 回调记录
     */
    public OperateNotify selectOperateNotifyByOrderId(Long orderId);

    /**
     * 查询回调记录列表
     * 
     * @param operateNotify 回调记录
     * @return 回调记录集合
     */
    public List<OperateNotify> selectOperateNotifyList(OperateNotify operateNotify);

    /**
     * 新增回调记录
     * 
     * @param operateNotify 回调记录
     * @return 结果
     */
    public int insertOperateNotify(OperateNotify operateNotify);

    /**
     * 修改回调记录
     * 
     * @param operateNotify 回调记录
     * @return 结果
     */
    public int updateOperateNotify(OperateNotify operateNotify);

    /**
     * 批量删除回调记录
     * 
     * @param ids 需要删除的回调记录主键集合
     * @return 结果
     */
    public int deleteOperateNotifyByIds(Long[] ids);

    /**
     * 删除回调记录信息
     * 
     * @param id 回调记录主键
     * @return 结果
     */
    public int deleteOperateNotifyById(Long id);
}

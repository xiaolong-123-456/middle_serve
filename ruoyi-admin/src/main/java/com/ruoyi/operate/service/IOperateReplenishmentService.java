package com.ruoyi.operate.service;

import com.ruoyi.operate.domain.OperateReplenishment;

import java.util.List;

/**
 * 补单记录Service接口
 * 
 * @author master123
 * @date 2024-12-25
 */
public interface IOperateReplenishmentService 
{
    /**
     * 查询补单记录
     * 
     * @param id 补单记录主键
     * @return 补单记录
     */
    public OperateReplenishment selectOperateReplenishmentById(Long id);

    /**
     * 查询补单记录列表
     * 
     * @param operateReplenishment 补单记录
     * @return 补单记录集合
     */
    public List<OperateReplenishment> selectOperateReplenishmentList(OperateReplenishment operateReplenishment);

    /**
     * 新增补单记录
     * 
     * @param operateReplenishment 补单记录
     * @return 结果
     */
    public int insertOperateReplenishment(OperateReplenishment operateReplenishment);

    /**
     * 修改补单记录
     * 
     * @param operateReplenishment 补单记录
     * @return 结果
     */
    public int updateOperateReplenishment(OperateReplenishment operateReplenishment);

    /**
     * 批量删除补单记录
     * 
     * @param ids 需要删除的补单记录主键集合
     * @return 结果
     */
    public int deleteOperateReplenishmentByIds(Long[] ids);

    /**
     * 删除补单记录信息
     * 
     * @param id 补单记录主键
     * @return 结果
     */
    public int deleteOperateReplenishmentById(Long id);
}

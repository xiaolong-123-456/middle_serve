package com.ruoyi.operate.service;

import com.ruoyi.operate.domain.OperateAgentProduct;

import java.util.List;

/**
 * 代理商和产品配置Service接口
 * 
 * @author master123
 * @date 2024-09-20
 */
public interface IOperateAgentProductService 
{
    /**
     * 查询代理商和产品配置
     * 
     * @param id 代理商和产品配置主键
     * @return 代理商和产品配置
     */
    public OperateAgentProduct selectOperateAgentProductById(Long id);

    /**
     * 查询代理商和产品配置列表
     * 
     * @param operateAgentProduct 代理商和产品配置
     * @return 代理商和产品配置集合
     */
    public List<OperateAgentProduct> selectOperateAgentProductList(OperateAgentProduct operateAgentProduct);

    /**
     * 新增代理商和产品配置
     * 
     * @param operateAgentProduct 代理商和产品配置
     * @return 结果
     */
    public int insertOperateAgentProduct(OperateAgentProduct operateAgentProduct);

    /**
     * 修改代理商和产品配置
     * 
     * @param operateAgentProduct 代理商和产品配置
     * @return 结果
     */
    public int updateOperateAgentProduct(OperateAgentProduct operateAgentProduct);

    /**
     * 批量删除代理商和产品配置
     * 
     * @param ids 需要删除的代理商和产品配置主键集合
     * @return 结果
     */
    public int deleteOperateAgentProductByIds(Long[] ids);

    /**
     * 删除代理商和产品配置信息
     * 
     * @param id 代理商和产品配置主键
     * @return 结果
     */
    public int deleteOperateAgentProductById(Long id);
}

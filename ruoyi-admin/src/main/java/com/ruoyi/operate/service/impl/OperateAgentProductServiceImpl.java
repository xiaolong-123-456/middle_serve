package com.ruoyi.operate.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.operate.domain.OperateAgentProduct;
import com.ruoyi.operate.mapper.OperateAgentProductMapper;
import com.ruoyi.operate.service.IOperateAgentProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 代理商和产品配置Service业务层处理
 * 
 * @author master123
 * @date 2024-09-20
 */
@Service
public class OperateAgentProductServiceImpl implements IOperateAgentProductService 
{
    @Autowired
    private OperateAgentProductMapper operateAgentProductMapper;

    /**
     * 查询代理商和产品配置
     * 
     * @param id 代理商和产品配置主键
     * @return 代理商和产品配置
     */
    @Override
    public OperateAgentProduct selectOperateAgentProductById(Long id)
    {
        return operateAgentProductMapper.selectOperateAgentProductById(id);
    }

    /**
     * 查询代理商和产品配置列表
     * 
     * @param operateAgentProduct 代理商和产品配置
     * @return 代理商和产品配置
     */
    @Override
    public List<OperateAgentProduct> selectOperateAgentProductList(OperateAgentProduct operateAgentProduct)
    {
        return operateAgentProductMapper.selectOperateAgentProductList(operateAgentProduct);
    }

    /**
     * 新增代理商和产品配置
     * 
     * @param operateAgentProduct 代理商和产品配置
     * @return 结果
     */
    @Override
    public int insertOperateAgentProduct(OperateAgentProduct operateAgentProduct)
    {
        operateAgentProduct.setCreateTime(DateUtils.getNowDate());
        return operateAgentProductMapper.insertOperateAgentProduct(operateAgentProduct);
    }

    /**
     * 修改代理商和产品配置
     * 
     * @param operateAgentProduct 代理商和产品配置
     * @return 结果
     */
    @Override
    public int updateOperateAgentProduct(OperateAgentProduct operateAgentProduct)
    {
        operateAgentProduct.setUpdateTime(DateUtils.getNowDate());
        return operateAgentProductMapper.updateOperateAgentProduct(operateAgentProduct);
    }

    /**
     * 批量删除代理商和产品配置
     * 
     * @param ids 需要删除的代理商和产品配置主键
     * @return 结果
     */
    @Override
    public int deleteOperateAgentProductByIds(Long[] ids)
    {
        return operateAgentProductMapper.deleteOperateAgentProductByIds(ids);
    }

    /**
     * 删除代理商和产品配置信息
     * 
     * @param id 代理商和产品配置主键
     * @return 结果
     */
    @Override
    public int deleteOperateAgentProductById(Long id)
    {
        return operateAgentProductMapper.deleteOperateAgentProductById(id);
    }
}
